from typing import Optional, List, Dict, Any
from openai import OpenAI
from anthropic import Anthropic
import os
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain
from langchain_community.llms import OpenAI as LangChainOpenAI

class LLMIntegration:
    """统一的 LLM 服务集成"""
    
    def __init__(self):
        self.openai_client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
        self.anthropic_client = Anthropic(api_key=os.getenv("ANTHROPIC_API_KEY"))
        
    async def call_openai(
        self, 
        prompt: str, 
        model: str = "gpt-4-turbo-preview",
        temperature: float = 0.7,
        max_tokens: int = 1000
    ) -> Dict[str, Any]:
        """调用 OpenAI API"""
        try:
            response = self.openai_client.chat.completions.create(
                model=model,
                messages=[{"role": "user", "content": prompt}],
                temperature=temperature,
                max_tokens=max_tokens
            )
            return {
                "success": True,
                "content": response.choices[0].message.content,
                "usage": response.usage.model_dump(),
                "model": model
            }
        except Exception as e:
            return {"success": False, "error": str(e)}
    
    async def call_claude(
        self, 
        prompt: str, 
        model: str = "claude-3-opus-20240229",
        max_tokens: int = 1000
    ) -> Dict[str, Any]:
        """调用 Claude API"""
        try:
            message = self.anthropic_client.messages.create(
                model=model,
                max_tokens=max_tokens,
                messages=[{"role": "user", "content": prompt}]
            )
            return {
                "success": True,
                "content": message.content[0].text,
                "usage": {
                    "input_tokens": message.usage.input_tokens,
                    "output_tokens": message.usage.output_tokens
                },
                "model": model
            }
        except Exception as e:
            return {"success": False, "error": str(e)}
    
    async def call_with_fallback(
        self, 
        prompt: str, 
        primary: str = "openai"
    ) -> Dict[str, Any]:
        """带降级的 LLM 调用"""
        if primary == "openai":
            result = await self.call_openai(prompt)
            if not result["success"]:
                # 降级到 Claude
                result = await self.call_claude(prompt)
        else:
            result = await self.call_claude(prompt)
            if not result["success"]:
                # 降级到 OpenAI
                result = await self.call_openai(prompt)
        return result