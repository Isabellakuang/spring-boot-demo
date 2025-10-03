from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List, Dict, Any
import logging
import sys
import os

# 添加当前目录到 Python 路径
sys.path.insert(0, os.path.dirname(__file__))

from llm_integration import LLMIntegration
from rag_engine import RAGEngine
from vector_store import VectorStoreManager

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="ML & AI Service - FREE Demo Version",
    description="AI/ML 服务 - 完全免费版本（无需API密钥）",
    version="3.0.0-free"
)

# CORS 配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://localhost:8080", "*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 初始化服务
logger.info("Initializing services in FREE mode...")
llm_service = LLMIntegration()
rag_engine = RAGEngine(vector_store_provider="chroma")
vector_store = VectorStoreManager(provider="chroma")
logger.info("All services initialized successfully!")

# ===== Pydantic Models =====

class LLMRequest(BaseModel):
    prompt: str
    model: Optional[str] = "mock-model"
    provider: Optional[str] = "mock"
    temperature: Optional[float] = 0.7
    max_tokens: Optional[int] = 1000

class DocumentIndexRequest(BaseModel):
    document: str
    metadata: Dict[str, Any]
    doc_id: str

class RAGQueryRequest(BaseModel):
    question: str
    top_k: Optional[int] = 5
    llm_provider: Optional[str] = "mock"
    include_sources: Optional[bool] = True

class VectorSearchRequest(BaseModel):
    query: str
    top_k: Optional[int] = 5
    filter: Optional[Dict] = None

# ===== LLM Endpoints =====

@app.post("/api/llm/generate")
async def generate_text(request: LLMRequest):
    """
    生成文本 (使用模拟LLM - 无需API密钥)
    
    这是一个演示端点，展示了LLM集成的架构设计。
    在生产环境中，这里会调用真实的LLM API（OpenAI、Anthropic等）。
    """
    try:
        result = await llm_service.call_mock_llm(
            prompt=request.prompt,
            model=request.model,
            temperature=request.temperature,
            max_tokens=request.max_tokens
        )
        return result
    except Exception as e:
        logger.error(f"LLM generation error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/llm/chat")
async def chat(request: LLMRequest):
    """聊天接口 - 模拟对话"""
    try:
        result = await llm_service.call_mock_llm(
            prompt=request.prompt,
            model=request.model
        )
        return {
            "message": result.get("content"),
            "model": request.model,
            "usage": result.get("usage"),
            "note": "This is a mock chat response for demonstration"
        }
    except Exception as e:
        logger.error(f"Chat error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/llm/models")
async def list_models():
    """列出可用的模型"""
    return {
        "models": [
            {
                "id": "mock-model",
                "name": "Mock Model (Free Demo)",
                "description": "Rule-based mock model for demonstration",
                "provider": "local",
                "cost": "FREE"
            }
        ],
        "note": "In production, this would list real LLM models from OpenAI, Anthropic, etc."
    }

# ===== RAG Endpoints =====

@app.post("/api/rag/index")
async def index_document(request: DocumentIndexRequest):
    """
    索引文档到向量数据库
    
    使用免费的本地嵌入模型和ChromaDB
    """
    try:
        result = await rag_engine.index_document(
            document=request.document,
            metadata=request.metadata,
            doc_id=request.doc_id
        )
        return result
    except Exception as e:
        logger.error(f"Document indexing error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/rag/query")
async def rag_query(request: RAGQueryRequest):
    """
    RAG 查询 - 检索增强生成
    
    使用本地向量搜索 + 模拟LLM响应
    """
    try:
        result = await rag_engine.query(
            question=request.question,
            top_k=request.top_k,
            llm_provider=request.llm_provider,
            include_sources=request.include_sources
        )
        return result
    except Exception as e:
        logger.error(f"RAG query error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/rag/stats")
async def rag_stats():
    """获取RAG引擎统计信息"""
    try:
        stats = await rag_engine.get_stats()
        return stats
    except Exception as e:
        logger.error(f"Stats error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# ===== Vector Store Endpoints =====

@app.post("/api/vector/search")
async def vector_search(request: VectorSearchRequest):
    """
    向量语义搜索
    
    使用免费的本地嵌入模型进行语义搜索
    """
    try:
        results = await vector_store.search(
            query=request.query,
            top_k=request.top_k,
            filter=request.filter
        )
        return {"results": results}
    except Exception as e:
        logger.error(f"Vector search error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/vector/stats")
async def vector_stats():
    """获取向量数据库统计信息"""
    try:
        stats = await vector_store.get_stats()
        return stats
    except Exception as e:
        logger.error(f"Vector stats error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# ===== Health Check =====

@app.get("/health")
async def health_check():
    """健康检查"""
    return {
        "status": "healthy",
        "version": "3.0.0-free",
        "mode": "FREE - No API keys required",
        "services": {
            "llm": "active (mock mode)",
            "rag": "active (local embeddings)",
            "vector_store": "active (ChromaDB)"
        },
        "features": {
            "text_generation": "enabled (rule-based)",
            "document_indexing": "enabled",
            "semantic_search": "enabled",
            "rag_query": "enabled",
            "api_keys_required": False
        }
    }

@app.get("/")
async def root():
    """根路径"""
    return {
        "service": "ML & AI Service",
        "version": "3.0.0-free",
        "description": "完全免费的AI/ML演示服务",
        "documentation": "/docs",
        "health": "/health",
        "note": "This is a FREE demo version. No API keys required!"
    }

if __name__ == "__main__":
    import uvicorn
    logger.info("Starting ML Service in FREE mode on port 8001...")
    uvicorn.run(app, host="0.0.0.0", port=8001)
