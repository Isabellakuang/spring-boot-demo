from typing import List, Dict, Any, Optional
import pinecone
import weaviate
import chromadb
from chromadb.config import Settings
import numpy as np
from openai import OpenAI

class VectorStoreManager:
    """统一的向量数据库管理"""
    
    def __init__(self, provider: str = "pinecone"):
        self.provider = provider
        self.openai_client = OpenAI()
        
        if provider == "pinecone":
            self._init_pinecone()
        elif provider == "weaviate":
            self._init_weaviate()
        elif provider == "chroma":
            self._init_chroma()
    
    def _init_pinecone(self):
        """初始化 Pinecone"""
        pinecone.init(
            api_key=os.getenv("PINECONE_API_KEY"),
            environment=os.getenv("PINECONE_ENV")
        )
        self.index_name = "knowledge-base"
        if self.index_name not in pinecone.list_indexes():
            pinecone.create_index(
                name=self.index_name,
                dimension=1536,  # OpenAI embedding dimension
                metric="cosine"
            )
        self.index = pinecone.Index(self.index_name)
    
    def _init_weaviate(self):
        """初始化 Weaviate"""
        self.client = weaviate.Client(
            url=os.getenv("WEAVIATE_URL", "http://localhost:8080"),
            auth_client_secret=weaviate.AuthApiKey(
                api_key=os.getenv("WEAVIATE_API_KEY")
            )
        )
        
        # 创建 Schema
        schema = {
            "class": "KnowledgeBase",
            "vectorizer": "none",
            "properties": [
                {"name": "content", "dataType": ["text"]},
                {"name": "metadata", "dataType": ["text"]},
                {"name": "source", "dataType": ["string"]},
            ]
        }
        
        if not self.client.schema.exists("KnowledgeBase"):
            self.client.schema.create_class(schema)
    
    def _init_chroma(self):
        """初始化 ChromaDB (本地开发用)"""
        self.client = chromadb.Client(Settings(
            chroma_db_impl="duckdb+parquet",
            persist_directory="./chroma_db"
        ))
        self.collection = self.client.get_or_create_collection(
            name="knowledge_base"
        )
    
    def get_embedding(self, text: str) -> List[float]:
        """获取文本嵌入向量"""
        response = self.openai_client.embeddings.create(
            model="text-embedding-3-small",
            input=text
        )
        return response.data[0].embedding
    
    async def upsert(
        self, 
        id: str, 
        text: str, 
        metadata: Dict[str, Any]
    ):
        """插入或更新向量"""
        embedding = self.get_embedding(text)
        
        if self.provider == "pinecone":
            self.index.upsert([(id, embedding, metadata)])
        
        elif self.provider == "weaviate":
            self.client.data_object.create(
                data_object={
                    "content": text,
                    "metadata": str(metadata),
                    "source": metadata.get("source", "unknown")
                },
                class_name="KnowledgeBase",
                vector=embedding,
                uuid=id
            )
        
        elif self.provider == "chroma":
            self.collection.upsert(
                ids=[id],
                embeddings=[embedding],
                documents=[text],
                metadatas=[metadata]
            )
    
    async def search(
        self, 
        query: str, 
        top_k: int = 5,
        filter: Optional[Dict] = None
    ) -> List[Dict[str, Any]]:
        """语义搜索"""
        query_embedding = self.get_embedding(query)
        
        if self.provider == "pinecone":
            results = self.index.query(
                vector=query_embedding,
                top_k=top_k,
                include_metadata=True,
                filter=filter
            )
            return [
                {
                    "id": match.id,
                    "score": match.score,
                    "metadata": match.metadata
                }
                for match in results.matches
            ]
        
        elif self.provider == "weaviate":
            results = (
                self.client.query
                .get("KnowledgeBase", ["content", "metadata", "source"])
                .with_near_vector({"vector": query_embedding})
                .with_limit(top_k)
                .do()
            )
            return results["data"]["Get"]["KnowledgeBase"]
        
        elif self.provider == "chroma":
            results = self.collection.query(
                query_embeddings=[query_embedding],
                n_results=top_k
            )
            return [
                {
                    "id": results["ids"][0][i],
                    "document": results["documents"][0][i],
                    "metadata": results["metadatas"][0][i],
                    "distance": results["distances"][0][i]
                }
                for i in range(len(results["ids"][0]))
            ]