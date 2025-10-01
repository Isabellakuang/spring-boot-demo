from typing import List, Dict, Any, Optional
from .vector_store import VectorStoreManager
from .llm_integration import LLMIntegration
from langchain.text_splitter import RecursiveCharacterTextSplitter

class RAGEngine:
    """检索增强生成 (RAG) 引擎"""
    
    def __init__(self, vector_store_provider: str = "pinecone"):
        self.vector_store = VectorStoreManager(provider=vector_store_provider)
        self.llm = LLMIntegration()
        self.text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=1000,
            chunk_overlap=200
        )
    
    async def index_document(
        self, 
        document: str, 
        metadata: Dict[str, Any],
        doc_id: str
    ):
        """索引文档到向量数据库"""
        chunks = self.text_splitter.split_text(document)
        
        for i, chunk in enumerate(chunks):
            chunk_id = f"{doc_id}_chunk_{i}"
            chunk_metadata = {
                **metadata,
                "chunk_index": i,
                "total_chunks": len(chunks)
            }
            await self.vector_store.upsert(chunk_id, chunk, chunk_metadata)
        
        return {"indexed_chunks": len(chunks)}
    
    async def query(
        self, 
        question: str,
        top_k: int = 5,
        llm_provider: str = "openai",
        include_sources: bool = True
    ) -> Dict[str, Any]:
        """RAG 查询"""
        
        # 1. 检索相关文档
        search_results = await self.vector_store.search(question, top_k=top_k)
        
        if not search_results:
            return {
                "answer": "抱歉，我没有找到相关信息来回答您的问题。",
                "sources": [],
                "confidence": 0.0
            }
        
        # 2. 构建上下文
        context = "\n\n".join([
            f"文档 {i+1}:\n{result.get('document', result.get('metadata', {}).get('content', ''))}"
            for i, result in enumerate(search_results)
        ])
        
        # 3. 构建提示词
        prompt = self._build_rag_prompt(question, context)
        
        # 4. 调用 LLM
        if llm_provider == "openai":
            llm_response = await self.llm.call_openai(prompt)
        else:
            llm_response = await self.llm.call_claude(prompt)
        
        # 5. 返回结果
        return {
            "answer": llm_response.get("content", ""),
            "sources": search_results if include_sources else [],
            "confidence": self._calculate_confidence(search_results),
            "usage": llm_response.get("usage", {})
        }
    
    def _build_rag_prompt(self, question: str, context: str) -> str:
        """构建 RAG 提示词"""
        return f"""基于以下上下文信息，回答用户的问题。如果上下文中没有相关信息，请明确说明。

上下文信息：
{context}

用户问题：{question}

请提供准确、简洁的回答，并在回答中引用相关的文档编号。
"""
    
    def _calculate_confidence(self, results: List[Dict]) -> float:
        """计算置信度"""
        if not results:
            return 0.0
        
        # 基于相似度分数计算平均置信度
        scores = [r.get("score", r.get("distance", 0)) for r in results]
        return min(sum(scores) / len(scores), 1.0)