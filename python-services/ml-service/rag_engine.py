from typing import List, Dict, Any, Optional
from vector_store import VectorStoreManager
from llm_integration import LLMIntegration
import logging

logger = logging.getLogger(__name__)

class RAGEngine:
    """检索增强生成 (RAG) 引擎 - 免费版本"""
    
    def __init__(self, vector_store_provider: str = "chroma"):
        self.vector_store = VectorStoreManager(provider="chroma")
        self.llm = LLMIntegration()
        logger.info("RAG Engine initialized in FREE mode")
    
    async def index_document(
        self, 
        document: str, 
        metadata: Dict[str, Any],
        doc_id: str
    ):
        """索引文档到向量数据库"""
        try:
            # 简单分块策略
            chunks = self._split_text(document, chunk_size=500, overlap=50)
            
            indexed_count = 0
            for i, chunk in enumerate(chunks):
                chunk_id = f"{doc_id}_chunk_{i}"
                chunk_metadata = {
                    **metadata,
                    "chunk_index": i,
                    "total_chunks": len(chunks),
                    "doc_id": doc_id
                }
                result = await self.vector_store.upsert(chunk_id, chunk, chunk_metadata)
                if result.get("success"):
                    indexed_count += 1
            
            logger.info(f"Indexed {indexed_count}/{len(chunks)} chunks for document {doc_id}")
            return {
                "success": True,
                "indexed_chunks": indexed_count,
                "total_chunks": len(chunks),
                "doc_id": doc_id
            }
        except Exception as e:
            logger.error(f"Document indexing error: {str(e)}")
            return {"success": False, "error": str(e)}
    
    def _split_text(self, text: str, chunk_size: int = 500, overlap: int = 50) -> List[str]:
        """简单的文本分块"""
        words = text.split()
        chunks = []
        
        for i in range(0, len(words), chunk_size - overlap):
            chunk = ' '.join(words[i:i + chunk_size])
            if chunk:
                chunks.append(chunk)
        
        return chunks if chunks else [text]
    
    async def query(
        self, 
        question: str,
        top_k: int = 5,
        llm_provider: str = "mock",
        include_sources: bool = True
    ) -> Dict[str, Any]:
        """RAG 查询 - 使用免费的本地模型"""
        try:
            # 1. 检索相关文档
            search_results = await self.vector_store.search(question, top_k=top_k)
            
            if not search_results:
                return {
                    "answer": "抱歉，我没有找到相关信息来回答您的问题。请尝试索引一些文档后再查询。",
                    "sources": [],
                    "confidence": 0.0,
                    "note": "No documents found in knowledge base"
                }
            
            # 2. 构建上下文
            context = "\n\n".join([
                f"文档片段 {i+1} (相似度: {result.get('score', 0):.2f}):\n{result.get('document', '')}"
                for i, result in enumerate(search_results)
            ])
            
            # 3. 构建提示词
            prompt = self._build_rag_prompt(question, context)
            
            # 4. 调用模拟 LLM
            llm_response = await self.llm.call_mock_llm(prompt)
            
            # 5. 返回结果
            return {
                "answer": llm_response.get("content", ""),
                "sources": search_results if include_sources else [],
                "confidence": self._calculate_confidence(search_results),
                "usage": llm_response.get("usage", {}),
                "note": llm_response.get("note", "")
            }
        except Exception as e:
            logger.error(f"RAG query error: {str(e)}")
            return {
                "success": False,
                "error": str(e),
                "answer": "查询过程中发生错误，请稍后重试。"
            }
    
    def _build_rag_prompt(self, question: str, context: str) -> str:
        """构建 RAG 提示词"""
        return f"""基于以下上下文信息，回答用户的问题。如果上下文中没有相关信息，请明确说明。

上下文信息：
{context}

用户问题：{question}

请提供准确、简洁的回答，并在回答中引用相关的文档片段编号。
"""
    
    def _calculate_confidence(self, results: List[Dict]) -> float:
        """计算置信度"""
        if not results:
            return 0.0
        
        # 基于相似度分数计算平均置信度
        scores = [r.get("score", 0) for r in results]
        avg_score = sum(scores) / len(scores) if scores else 0
        return min(avg_score, 1.0)
    
    async def get_stats(self) -> Dict[str, Any]:
        """获取 RAG 引擎统计信息"""
        try:
            vector_stats = await self.vector_store.get_stats()
            return {
                "rag_engine": "FREE version",
                "vector_store": vector_stats,
                "llm_provider": "Mock LLM (rule-based)",
                "features": {
                    "document_indexing": "enabled",
                    "semantic_search": "enabled (local embeddings)",
                    "rag_query": "enabled (mock responses)",
                    "api_keys_required": False
                }
            }
        except Exception as e:
            logger.error(f"Stats error: {str(e)}")
            return {"error": str(e)}
