from typing import List, Dict, Any, Optional
import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer
import logging
import os

logger = logging.getLogger(__name__)

class VectorStoreManager:
    """简化版向量数据库管理 - 仅使用 ChromaDB（免费本地方案）"""
    
    def __init__(self, provider: str = "chroma"):
        self.provider = "chroma"  # 强制使用 ChromaDB
        logger.info("Vector Store initialized with ChromaDB (FREE, no API keys required)")
        
        # 使用免费的本地嵌入模型
        self.embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
        logger.info("Using free local embedding model: all-MiniLM-L6-v2")
        
        self._init_chroma()
    
    def _init_chroma(self):
        """初始化 ChromaDB (完全免费的本地向量数据库)"""
        persist_dir = os.getenv("CHROMA_PERSIST_DIR", "./chroma_db")
        
        self.client = chromadb.Client(Settings(
            chroma_db_impl="duckdb+parquet",
            persist_directory=persist_dir
        ))
        
        self.collection = self.client.get_or_create_collection(
            name="knowledge_base",
            metadata={"description": "Free local knowledge base for demo"}
        )
        logger.info(f"ChromaDB collection initialized at {persist_dir}")
    
    def get_embedding(self, text: str) -> List[float]:
        """
        获取文本嵌入向量
        使用免费的本地模型 sentence-transformers
        """
        embedding = self.embedding_model.encode(text)
        return embedding.tolist()
    
    async def upsert(
        self, 
        id: str, 
        text: str, 
        metadata: Dict[str, Any]
    ):
        """插入或更新向量到 ChromaDB"""
        try:
            embedding = self.get_embedding(text)
            
            self.collection.upsert(
                ids=[id],
                embeddings=[embedding],
                documents=[text],
                metadatas=[metadata]
            )
            logger.info(f"Document upserted: {id}")
            return {"success": True, "id": id}
        except Exception as e:
            logger.error(f"Upsert error: {str(e)}")
            return {"success": False, "error": str(e)}
    
    async def search(
        self, 
        query: str, 
        top_k: int = 5,
        filter: Optional[Dict] = None
    ) -> List[Dict[str, Any]]:
        """
        语义搜索
        使用本地嵌入模型进行向量相似度搜索
        """
        try:
            query_embedding = self.get_embedding(query)
            
            results = self.collection.query(
                query_embeddings=[query_embedding],
                n_results=top_k,
                where=filter
            )
            
            # 格式化结果
            formatted_results = []
            if results and results["ids"] and len(results["ids"][0]) > 0:
                for i in range(len(results["ids"][0])):
                    formatted_results.append({
                        "id": results["ids"][0][i],
                        "document": results["documents"][0][i],
                        "metadata": results["metadatas"][0][i] if results["metadatas"] else {},
                        "distance": results["distances"][0][i] if results["distances"] else 0,
                        "score": 1 - results["distances"][0][i] if results["distances"] else 1.0
                    })
            
            logger.info(f"Search completed: {len(formatted_results)} results found")
            return formatted_results
            
        except Exception as e:
            logger.error(f"Search error: {str(e)}")
            return []
    
    async def delete(self, id: str):
        """删除文档"""
        try:
            self.collection.delete(ids=[id])
            logger.info(f"Document deleted: {id}")
            return {"success": True, "id": id}
        except Exception as e:
            logger.error(f"Delete error: {str(e)}")
            return {"success": False, "error": str(e)}
    
    async def get_stats(self) -> Dict[str, Any]:
        """获取向量数据库统计信息"""
        try:
            count = self.collection.count()
            return {
                "provider": "ChromaDB (Free Local)",
                "collection_name": "knowledge_base",
                "document_count": count,
                "embedding_model": "all-MiniLM-L6-v2",
                "embedding_dimension": 384,
                "note": "Using free local embedding model - no API keys required"
            }
        except Exception as e:
            logger.error(f"Stats error: {str(e)}")
            return {"error": str(e)}
