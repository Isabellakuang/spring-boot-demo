from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List, Dict, Any
from .llm_integration import LLMIntegration
from .rag_engine import RAGEngine
from .vector_store import VectorStoreManager
import logging

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="ML & LLM Service",
    description="AI/ML 和 LLM 集成服务",
    version="2.0.0"
)

# CORS 配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 初始化服务
llm_service = LLMIntegration()
rag_engine = RAGEngine(vector_store_provider="chroma")  # 本地开发用 chroma

# ===== Pydantic Models =====

class LLMRequest(BaseModel):
    prompt: str
    model: Optional[str] = "gpt-4-turbo-preview"
    provider: Optional[str] = "openai"
    temperature: Optional[float] = 0.7
    max_tokens: Optional[int] = 1000

class DocumentIndexRequest(BaseModel):
    document: str
    metadata: Dict[str, Any]
    doc_id: str

class RAGQueryRequest(BaseModel):
    question: str
    top_k: Optional[int] = 5
    llm_provider: Optional[str] = "openai"
    include_sources: Optional[bool] = True

class VectorSearchRequest(BaseModel):
    query: str
    top_k: Optional[int] = 5
    filter: Optional[Dict] = None

# ===== LLM Endpoints =====

@app.post("/api/llm/generate")
async def generate_text(request: LLMRequest):
    """生成文本 (OpenAI/Claude)"""
    try:
        if request.provider == "openai":
            result = await llm_service.call_openai(
                prompt=request.prompt,
                model=request.model,
                temperature=request.temperature,
                max_tokens=request.max_tokens
            )
        elif request.provider == "claude":
            result = await llm_service.call_claude(
                prompt=request.prompt,
                model=request.model,
                max_tokens=request.max_tokens
            )
        else:
            raise HTTPException(status_code=400, detail="Unsupported provider")
        
        return result
    except Exception as e:
        logger.error(f"LLM generation error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/llm/generate-with-fallback")
async def generate_with_fallback(request: LLMRequest):
    """带降级的文本生成"""
    try:
        result = await llm_service.call_with_fallback(
            prompt=request.prompt,
            primary=request.provider
        )
        return result
    except Exception as e:
        logger.error(f"LLM fallback error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# ===== RAG Endpoints =====

@app.post("/api/rag/index")
async def index_document(request: DocumentIndexRequest):
    """索引文档到向量数据库"""
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
    """RAG 查询"""
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

@app.post("/api/vector/search")
async def vector_search(request: VectorSearchRequest):
    """向量语义搜索"""
    try:
        results = await rag_engine.vector_store.search(
            query=request.query,
            top_k=request.top_k,
            filter=request.filter
        )
        return {"results": results}
    except Exception as e:
        logger.error(f"Vector search error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# ===== Health Check =====

@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "services": {
            "llm": "active",
            "rag": "active",
            "vector_store": "active"
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)