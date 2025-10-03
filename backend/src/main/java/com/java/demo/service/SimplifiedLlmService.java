package com.java.demo.service;

import com.java.demo.dto.LlmRequest;
import com.java.demo.dto.LlmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简化的LLM服务 - 使用规则引擎模拟
 * 适合本地演示，无需付费API密钥
 */
@Service
@Slf4j
public class SimplifiedLlmService {

    private static final Map<String, String> KNOWLEDGE_BASE = new HashMap<>();
    
    static {
        // 预定义的知识库
        KNOWLEDGE_BASE.put("spring boot", 
            "Spring Boot是一个基于Spring框架的开源Java框架，用于创建独立的、生产级的Spring应用程序。" +
            "主要特性包括：\n" +
            "1. 自动配置 - 根据类路径自动配置Spring应用\n" +
            "2. 起步依赖 - 简化Maven/Gradle配置\n" +
            "3. 内嵌服务器 - 无需部署WAR文件\n" +
            "4. 生产就绪功能 - 健康检查、指标监控等\n" +
            "5. 无代码生成和XML配置 - 使用注解和Java配置");
        
        KNOWLEDGE_BASE.put("微服务", 
            "微服务架构是一种将应用程序构建为一组小型服务的方法，每个服务运行在自己的进程中，" +
            "并通过轻量级机制（通常是HTTP API）进行通信。主要优势包括：\n" +
            "1. 独立部署和扩展\n" +
            "2. 技术栈灵活性\n" +
            "3. 故障隔离\n" +
            "4. 团队自治\n" +
            "5. 持续交付和部署");
        
        KNOWLEDGE_BASE.put("java", 
            "Java是一种面向对象的编程语言，具有跨平台、安全、健壮等特性。" +
            "主要特点：\n" +
            "1. 平台无关性 - 一次编写，到处运行\n" +
            "2. 面向对象 - 封装、继承、多态\n" +
            "3. 自动内存管理 - 垃圾回收机制\n" +
            "4. 丰富的API和生态系统\n" +
            "5. 强类型和编译时检查");
        
        KNOWLEDGE_BASE.put("rag", 
            "RAG（检索增强生成）是一种结合信息检索和文本生成的AI技术。" +
            "工作流程：\n" +
            "1. 文档索引 - 将文档转换为向量并存储\n" +
            "2. 语义检索 - 根据查询检索相关文档\n" +
            "3. 上下文构建 - 将检索结果作为上下文\n" +
            "4. 答案生成 - 基于上下文生成准确答案\n" +
            "优势：减少幻觉、提供可追溯的答案来源");
        
        KNOWLEDGE_BASE.put("nlp", 
            "NLP（自然语言处理）是人工智能的一个分支，专注于计算机与人类语言的交互。" +
            "主要任务包括：\n" +
            "1. 文本分类和情感分析\n" +
            "2. 命名实体识别\n" +
            "3. 机器翻译\n" +
            "4. 问答系统\n" +
            "5. 文本生成和摘要");
        
        KNOWLEDGE_BASE.put("rest api", 
            "REST API是一种基于HTTP协议的Web服务架构风格。" +
            "核心原则：\n" +
            "1. 无状态 - 每个请求包含所有必要信息\n" +
            "2. 统一接口 - 使用标准HTTP方法\n" +
            "3. 资源导向 - URL代表资源\n" +
            "4. 可缓存 - 支持HTTP缓存机制\n" +
            "5. 分层系统 - 支持负载均衡和代理");
        
        KNOWLEDGE_BASE.put("docker", 
            "Docker是一个开源的容器化平台，用于开发、部署和运行应用程序。" +
            "主要概念：\n" +
            "1. 镜像 - 应用程序的只读模板\n" +
            "2. 容器 - 镜像的运行实例\n" +
            "3. Dockerfile - 构建镜像的脚本\n" +
            "4. Docker Compose - 多容器应用编排\n" +
            "5. 容器编排 - Kubernetes等工具");
    }

    /**
     * 生成文本 - 接受 LlmRequest 对象
     */
    public LlmResponse generate(LlmRequest request) {
        try {
            String response = generateText(request.getPrompt(), request.getMaxTokens());
            
            return LlmResponse.builder()
                .success(true)
                .content(response)
                .response(response)
                .model("rule-based-engine")
                .tokensUsed(estimateTokens(response))
                .usage(Map.of(
                    "prompt_tokens", estimateTokens(request.getPrompt()),
                    "completion_tokens", estimateTokens(response),
                    "total_tokens", estimateTokens(request.getPrompt()) + estimateTokens(response)
                ))
                .generatedAt(LocalDateTime.now())
                .build();
        } catch (Exception e) {
            log.error("Error generating text: {}", e.getMessage(), e);
            return LlmResponse.builder()
                .success(false)
                .error(e.getMessage())
                .model("rule-based-engine")
                .generatedAt(LocalDateTime.now())
                .build();
        }
    }

    /**
     * 带降级的文本生成
     */
    public LlmResponse generateWithFallback(LlmRequest request) {
        // 在演示版本中，直接调用 generate 方法
        // 在生产环境中，这里会实现主LLM失败时的降级逻辑
        log.info("Using fallback strategy (demo mode - same as generate)");
        return generate(request);
    }

    /**
     * 对话式生成
     */
    public LlmResponse chat(String conversationId, LlmRequest request) {
        try {
            log.info("Chat for conversation: {}", conversationId);
            
            // 在演示版本中，简化处理
            String response = generateText(request.getPrompt(), request.getMaxTokens());
            
            return LlmResponse.builder()
                .success(true)
                .content(response)
                .response(response)
                .model("rule-based-engine")
                .tokensUsed(estimateTokens(response))
                .usage(Map.of(
                    "conversation_id", conversationId,
                    "prompt_tokens", estimateTokens(request.getPrompt()),
                    "completion_tokens", estimateTokens(response),
                    "total_tokens", estimateTokens(request.getPrompt()) + estimateTokens(response)
                ))
                .generatedAt(LocalDateTime.now())
                .build();
        } catch (Exception e) {
            log.error("Error in chat: {}", e.getMessage(), e);
            return LlmResponse.builder()
                .success(false)
                .error(e.getMessage())
                .model("rule-based-engine")
                .generatedAt(LocalDateTime.now())
                .build();
        }
    }

    /**
     * 生成文本 - 使用规则引擎
     */
    public String generateText(String prompt, int maxTokens) {
        log.info("Generating text for prompt: {}", prompt.substring(0, Math.min(50, prompt.length())));
        
        try {
            // 1. 关键词匹配
            String response = matchKeywords(prompt);
            
            // 2. 如果没有匹配，使用模板响应
            if (response == null) {
                response = generateTemplateResponse(prompt);
            }
            
            // 3. 限制长度
            if (response.length() > maxTokens * 4) {
                response = response.substring(0, maxTokens * 4) + "...";
            }
            
            log.info("Generated response with {} characters", response.length());
            return response;
            
        } catch (Exception e) {
            log.error("Error generating text: {}", e.getMessage(), e);
            return "抱歉，生成回复时发生错误。请重试。";
        }
    }

    /**
     * 聊天对话
     */
    public Map<String, Object> chat(String message, List<Map<String, String>> history) {
        String response = generateText(message, 500);
        
        return Map.of(
            "message", response,
            "model", "rule-based-engine",
            "usage", Map.of(
                "prompt_tokens", estimateTokens(message),
                "completion_tokens", estimateTokens(response),
                "total_tokens", estimateTokens(message) + estimateTokens(response)
            ),
            "timestamp", LocalDateTime.now()
        );
    }

    /**
     * 获取可用模型列表
     */
    public Map<String, Object> getAvailableModels() {
        return Map.of(
            "models", List.of(
                Map.of(
                    "id", "rule-based-engine",
                    "name", "规则引擎模型（演示版）",
                    "description", "基于规则的文本生成引擎，适合演示使用",
                    "provider", "local",
                    "cost", "免费",
                    "features", Arrays.asList("关键词匹配", "模板响应", "上下文理解")
                )
            ),
            "default", "rule-based-engine",
            "count", 1
        );
    }

    /**
     * 获取使用统计 - 支持过滤参数
     */
    public Map<String, Object> getUsageStats(String provider, String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", 0);
        stats.put("totalTokens", 0);
        stats.put("averageResponseTime", "< 100ms");
        stats.put("model", "rule-based-engine");
        stats.put("cost", "免费");
        stats.put("note", "这是演示版本，生产环境会使用真实的LLM API");
        
        // 添加过滤参数信息
        if (provider != null) {
            stats.put("filteredProvider", provider);
        }
        if (startDate != null) {
            stats.put("startDate", startDate);
        }
        if (endDate != null) {
            stats.put("endDate", endDate);
        }
        
        return stats;
    }

    /**
     * 获取使用统计 - 无参数版本
     */
    public Map<String, Object> getUsageStats() {
        return getUsageStats(null, null, null);
    }

    // ===== 私有辅助方法 =====

    /**
     * 关键词匹配
     */
    private String matchKeywords(String prompt) {
        String lowerPrompt = prompt.toLowerCase();
        
        // 遍历知识库查找匹配
        for (Map.Entry<String, String> entry : KNOWLEDGE_BASE.entrySet()) {
            if (lowerPrompt.contains(entry.getKey())) {
                return formatResponse(entry.getValue(), prompt);
            }
        }
        
        // 特殊问题处理
        if (isQuestionAbout(lowerPrompt, "什么是", "介绍", "解释")) {
            String topic = extractTopic(prompt);
            return String.format("关于'%s'：这是一个技术概念。在实际应用中，我会从知识库中检索相关信息并提供详细解答。", topic);
        }
        
        if (isQuestionAbout(lowerPrompt, "如何", "怎么", "怎样")) {
            return "这是一个操作性问题。在实际应用中，我会提供详细的步骤指导。" +
                   "建议您查阅相关文档或在知识库中索引相关教程文档。";
        }
        
        if (isQuestionAbout(lowerPrompt, "为什么", "原因")) {
            return "这是一个原理性问题。在实际应用中，我会分析原因并提供深入解释。" +
                   "建议您在知识库中索引相关技术文档以获得更准确的答案。";
        }
        
        return null;
    }

    /**
     * 生成模板响应
     */
    private String generateTemplateResponse(String prompt) {
        // 提取关键词
        List<String> keywords = extractKeywords(prompt);
        
        if (keywords.isEmpty()) {
            return "感谢您的提问。这是一个演示系统，使用规则引擎模拟LLM响应。" +
                   "在生产环境中，这里会调用真实的LLM API（如OpenAI GPT-4、Anthropic Claude等）来生成更智能的回复。\n\n" +
                   "当前系统支持的主题包括：Spring Boot、微服务、Java、RAG、NLP、REST API、Docker等。" +
                   "您可以尝试询问这些主题相关的问题。";
        }
        
        return String.format(
            "关于您提到的 %s，这是一个重要的技术话题。\n\n" +
            "在实际的生产环境中，系统会：\n" +
            "1. 使用真实的LLM API生成详细回答\n" +
            "2. 从向量数据库检索相关文档\n" +
            "3. 结合上下文提供准确的答案\n\n" +
            "当前演示版本使用规则引擎，主要展示系统架构和集成方式。" +
            "建议在知识库中索引相关文档，然后使用RAG查询功能获得更准确的答案。",
            String.join("、", keywords)
        );
    }

    /**
     * 格式化响应
     */
    private String formatResponse(String baseResponse, String prompt) {
        return String.format(
            "%s\n\n" +
            "---\n" +
            "💡 提示：这是基于规则引擎的演示响应。在生产环境中，系统会使用真实的LLM API提供更智能、更准确的回答。",
            baseResponse
        );
    }

    /**
     * 判断是否是特定类型的问题
     */
    private boolean isQuestionAbout(String prompt, String... patterns) {
        for (String pattern : patterns) {
            if (prompt.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 提取主题
     */
    private String extractTopic(String prompt) {
        // 简单的主题提取
        Pattern pattern = Pattern.compile("(?:什么是|介绍|解释)\\s*([\\u4e00-\\u9fa5a-zA-Z\\s]+)");
        Matcher matcher = pattern.matcher(prompt);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return "该主题";
    }

    /**
     * 提取关键词
     */
    private List<String> extractKeywords(String text) {
        List<String> keywords = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (String keyword : KNOWLEDGE_BASE.keySet()) {
            if (lowerText.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        
        return keywords;
    }

    /**
     * 估算token数量
     */
    private int estimateTokens(String text) {
        // 简单估算：中文字符约1.5个token，英文单词约1个token
        int chineseChars = text.replaceAll("[^\\u4e00-\\u9fa5]", "").length();
        int englishWords = text.split("\\s+").length;
        
        return (int) (chineseChars * 1.5 + englishWords);
    }
}
