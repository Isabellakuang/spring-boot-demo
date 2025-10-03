from typing import Optional, List, Dict, Any
import logging

logger = logging.getLogger(__name__)

class LLMIntegration:
    """简化版 LLM 服务 - 使用模拟响应（无需付费API）"""
    
    def __init__(self):
        logger.info("LLM Integration initialized in FREE mode (no API keys required)")
        
    async def call_mock_llm(
        self, 
        prompt: str, 
        model: str = "mock-model",
        temperature: float = 0.7,
        max_tokens: int = 1000
    ) -> Dict[str, Any]:
        """模拟 LLM 调用 - 用于演示目的"""
        try:
            # 简单的基于规则的响应
            response_content = self._generate_mock_response(prompt)
            
            return {
                "success": True,
                "content": response_content,
                "usage": {
                    "prompt_tokens": len(prompt.split()),
                    "completion_tokens": len(response_content.split()),
                    "total_tokens": len(prompt.split()) + len(response_content.split())
                },
                "model": model,
                "note": "This is a mock response for demonstration purposes. No API key required."
            }
        except Exception as e:
            logger.error(f"Mock LLM error: {str(e)}")
            return {"success": False, "error": str(e)}
    
    def _generate_mock_response(self, prompt: str) -> str:
        """生成基于规则的模拟响应"""
        prompt_lower = prompt.lower()
        
        # 简单的关键词匹配
        if "hello" in prompt_lower or "你好" in prompt_lower or "hi" in prompt_lower:
            return "您好！我是演示版AI助手。这是一个模拟响应，用于展示系统架构。在生产环境中，这里会调用真实的LLM API。"
        
        elif "spring boot" in prompt_lower:
            return "Spring Boot是一个基于Spring框架的开源Java应用程序框架，它简化了Spring应用的初始搭建以及开发过程。主要特性包括：自动配置、起步依赖、内嵌服务器、生产就绪功能等。"
        
        elif "react" in prompt_lower:
            return "React是由Facebook开发的用于构建用户界面的JavaScript库。它采用组件化开发模式，使用虚拟DOM提高性能，支持单向数据流，是目前最流行的前端框架之一。"
        
        elif "docker" in prompt_lower or "kubernetes" in prompt_lower:
            return "Docker是容器化技术的代表，Kubernetes是容器编排平台。它们共同构成了现代云原生应用的基础设施，实现了应用的快速部署、扩展和管理。"
        
        elif "数据" in prompt_lower or "data" in prompt_lower:
            return "数据工程涉及数据的收集、存储、处理和分析。现代数据平台通常包括：数据仓库、数据湖、ETL管道、数据质量检查、实时处理等组件。"
        
        elif "?" in prompt or "？" in prompt or "what" in prompt_lower or "how" in prompt_lower:
            return f"这是一个演示响应。您的问题是：'{prompt[:100]}...'。在实际应用中，这里会调用真实的LLM API（如OpenAI GPT-4）来生成智能回答。当前系统展示了完整的架构设计，包括API接口、数据流、错误处理等企业级特性。"
        
        else:
            return f"感谢您的输入。这是一个模拟的AI响应，用于演示系统功能。实际部署时，可以集成OpenAI、Anthropic等LLM服务。当前展示的是完整的微服务架构、RESTful API设计和错误处理机制。"
    
    async def call_with_fallback(
        self, 
        prompt: str, 
        primary: str = "mock"
    ) -> Dict[str, Any]:
        """模拟带降级的 LLM 调用"""
        return await self.call_mock_llm(prompt)
