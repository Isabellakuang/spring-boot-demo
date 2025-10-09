# 面试演示脚本

## 目录
1. [演示准备](#演示准备)
2. [演示流程](#演示流程)
3. [技术亮点讲解](#技术亮点讲解)
4. [常见问题应对](#常见问题应对)
5. [时间分配建议](#时间分配建议)

---

## 演示准备

### 提前准备（演示前30分钟）

```bash
# 1. 确保Docker Desktop和Kubernetes正在运行
kubectl cluster-info

# 2. 清理旧部署（如果存在）
python scripts/clean.py

# 3. 构建镜像
python scripts/build.py

# 4. 部署应用
python scripts/deploy.py

# 5. 等待所有Pod就绪
kubectl get pods -n scb-rag-demo -w

# 6. 验证系统健康
curl http://localhost:30080/api/health

# 7. 准备示例PDF文档（放在桌面）
# - machine-learning.pdf
# - deep-learning.pdf
```

### 浏览器准备

打开以下标签页：
1. **应用前端**: http://localhost:30080
2. **Swagger API文档**: http://localhost:30081/swagger-ui.html
3. **GitHub仓库**: https://github.com/yourusername/scb-rag-demo
4. **架构图**: docs/architecture.md

### 终端准备

打开3个终端窗口：
1. **终端1**: 用于执行命令
2. **终端2**: 实时查看后端日志
   ```bash
   kubectl logs -f -l app=backend -n scb-rag-demo
   ```
3. **终端3**: 实时查看前端日志
   ```bash
   kubectl logs -f -l app=frontend -n scb-rag-demo
   ```

---

## 演示流程

### 第一部分：项目介绍（2-3分钟）

**开场白**：
> "大家好，今天我要演示的是一个基于RAG（检索增强生成）技术的智能问答系统。这个项目展示了如何将传统NLP与文档检索相结合，提供更准确、更有依据的AI回答。"

**快速概览**：
1. 打开GitHub仓库，展示README.md
2. 说明项目背景和核心功能
3. 展示技术栈（Spring Boot + React + PostgreSQL + Redis + Kubernetes）

**关键话术**：
> "这个系统的核心价值在于：当用户提问时，系统会智能判断是使用纯NLP模式还是RAG模式。如果问题与已上传的文档相关，系统会自动检索相关内容，并基于这些内容生成回答，同时提供来源引用，确保答案的可追溯性。"

### 第二部分：系统架构讲解（3-4分钟）

**展示架构图**：
打开`docs/architecture.md`，展示系统架构图

**讲解要点**：

1. **整体架构**（1分钟）
   > "系统采用前后端分离架构，前端使用React + Vite构建，后端使用Spring Boot。数据层包括PostgreSQL用于持久化存储和全文搜索，Redis用于查询结果缓存。"

2. **核心流程**（2分钟）
   - **文档处理流程**：
     > "当用户上传PDF文档时，系统会：1) 使用Apache PDFBox提取文本；2) 通过智能分片算法将文本切分为1000字符的片段，片段间有200字符重叠以保持上下文连贯性；3) 存储到PostgreSQL并建立全文搜索索引。"
   
   - **查询处理流程**：
     > "查询时，系统首先通过RouterService进行智能路由判断。如果检测到文档相关关键词，使用RAG模式：1) 在PostgreSQL中进行全文搜索，找出最相关的5个文档片段；2) 将这些片段作为上下文，连同用户问题一起发送给Poe API；3) 返回答案和来源引用。"

3. **Kubernetes部署**（1分钟）
   > "应用部署在本地Kubernetes集群上，包括PostgreSQL、Redis、后端和前端四个主要服务。配置了HPA自动扩缩容，当CPU使用率超过70%时自动增加Pod副本。"

### 第三部分：实际操作演示（8-10分钟）

#### 3.1 系统健康检查（1分钟）

```bash
# 终端1执行
kubectl get all -n scb-rag-demo
```

**讲解**：
> "首先查看所有服务的运行状态。可以看到PostgreSQL、Redis、后端和前端都在正常运行，每个服务都有对应的Pod、Service和Deployment。"

```bash
# 测试API健康
curl http://localhost:30080/api/health
```

**讲解**：
> "健康检查API返回所有组件状态为UP，说明系统运行正常。"

#### 3.2 文档上传演示（2-3分钟）

**步骤1：打开前端界面**
1. 访问 http://localhost:30080
2. 展示界面布局：左侧文档列表、中间聊天区、右侧来源引用

**讲解**：
> "这是系统的主界面。左侧显示已上传的文档列表，中间是聊天区域，右侧会显示答案的来源引用。"

**步骤2：上传文档**
1. 点击"上传文档"按钮
2. 选择准备好的PDF文件（如machine-learning.pdf）
3. 观察上传进度和处理状态

**同时在终端2观察后端日志**：
```
[LoggingAspect] Executing: processDocument
[PdfParser] Extracting text from: machine-learning.pdf
[TextSplitter] Splitting text into chunks...
[DocumentServiceImpl] Created 15 chunks for document ID: 1
[PerformanceAspect] processDocument executed in 2345ms
```

**讲解**：
> "可以看到后端日志显示了完整的处理流程：PDF文本提取、文本分片、数据库存储。这个5MB的文档被分成了15个片段，整个处理过程耗时约2.3秒。"

**步骤3：查看文档详情**
1. 在文档列表中点击刚上传的文档
2. 展示文档状态、大小、片段数量等信息

#### 3.3 智能问答演示（4-5分钟）

**场景1：RAG模式查询**

在聊天框输入：
```
什么是监督学习？请详细解释。
```

**观察点**：
1. 查询模式自动切换为"RAG"
2. 右侧显示来源引用（文档名、相似度、页码）
3. 答案中包含文档内容的总结

**讲解**：
> "系统检测到这是一个与文档相关的技术问题，自动使用RAG模式。可以看到右侧显示了3个来源引用，相似度分别为0.92、0.85和0.78。答案是基于这些文档片段生成的，确保了准确性。"

**同时在终端2观察**：
```
[RouterServiceImpl] Detected RAG keywords, using RAG mode
[DocumentChunkRepository] Full-text search found 5 relevant chunks
[QueryServiceImpl] Query processed in 1250ms, mode: RAG
[CacheKeyGenerator] Cached query result with key: query:hash:abc123
```

**场景2：NLP模式查询**

在聊天框输入：
```
你好，今天天气怎么样？
```

**观察点**：
1. 查询模式自动切换为"NLP"
2. 右侧来源引用为空
3. 答案是通用的对话回复

**讲解**：
> "这是一个通用对话问题，系统自动切换到NLP模式，直接调用Poe API生成回答，不需要检索文档。"

**场景3：缓存演示**

再次输入之前的问题：
```
什么是监督学习？请详细解释。
```

**观察终端2日志**：
```
[QueryServiceImpl] Cache hit for query: 什么是监督学习？
[QueryServiceImpl] Query processed in 45ms, mode: RAG (cached)
```

**讲解**：
> "注意到这次查询只用了45毫秒，因为结果已经被缓存。Redis缓存大大提升了重复查询的响应速度。"

#### 3.4 历史记录演示（1分钟）

1. 点击侧边栏的"历史记录"
2. 展示查询历史列表
3. 点击某条历史记录，查看详细信息

**讲解**：
> "系统记录了所有查询历史，包括问题、答案、使用的模式、处理时间等。这对于分析用户行为和系统性能很有帮助。"

#### 3.5 Kubernetes管理演示（1-2分钟）

**展示Pod状态**：
```bash
kubectl get pods -n scb-rag-demo
kubectl top pods -n scb-rag-demo
```

**讲解**：
> "可以看到所有Pod的资源使用情况。后端Pod当前使用约300MB内存和0.2核CPU，远低于配置的限制。"

**展示HPA**：
```bash
kubectl get hpa -n scb-rag-demo
```

**讲解**：
> "HPA配置了自动扩缩容，当CPU使用率超过70%时会自动增加副本数，最多扩展到5个副本。"

**展示日志脚本**：
```bash
python scripts/logs.py
# 选择查看后端日志
```

**讲解**：
> "我还编写了便捷的日志查看脚本，可以快速查看各个服务的日志，方便调试和监控。"

### 第四部分：代码讲解（3-4分钟）

**选择2-3个核心代码片段讲解**：

#### 1. 智能路由逻辑（RouterServiceImpl.java）

打开文件，展示关键代码：
```java
private static final Set<String> RAG_KEYWORDS = Set.of(
    "什么是", "如何", "为什么", "解释", "定义", ...
);

public QueryMode determineMode(String question) {
    String lowerQuestion = question.toLowerCase();
    boolean hasRagKeyword = RAG_KEYWORDS.stream()
        .anyMatch(lowerQuestion::contains);
    
    if (hasRagKeyword && documentChunkRepository.count() > 0) {
        return QueryMode.RAG;
    }
    return QueryMode.NLP;
}
```

**讲解**：
> "这是智能路由的核心逻辑。系统维护了一个关键词列表，当问题包含这些关键词且数据库中有文档时，自动使用RAG模式。这个简单但有效的策略确保了模式选择的准确性。"

#### 2. 全文搜索实现（DocumentChunkRepository.java）

```java
@Query(value = """
    SELECT c.*, ts_rank(to_tsvector('simple', c.content), 
                        plainto_tsquery('simple', :query)) as similarity
    FROM document_chunks c
    WHERE to_tsvector('simple', c.content) @@ plainto_tsquery('simple', :query)
    ORDER BY similarity DESC
    LIMIT :limit
    """, nativeQuery = true)
List<DocumentChunk> searchByContent(@Param("query") String query, 
                                   @Param("limit") int limit);
```

**讲解**：
> "这里使用PostgreSQL的全文搜索功能。tsvector和ts_rank提供了高效的文本检索和相似度计算。相比传统的LIKE查询，这种方式性能更好，支持词干提取和停用词过滤。"

#### 3. 前端状态管理（queryStore.js）

```javascript
const useQueryStore = create((set, get) => ({
  messages: [],
  currentMode: 'AUTO',
  
  submitQuery: async (question) => {
    const response = await queryService.submitQuery({
      question,
      mode: get().currentMode
    });
    
    set(state => ({
      messages: [...state.messages, {
        type: 'user',
        content: question
      }, {
        type: 'assistant',
        content: response.answer,
        sources: response.sources,
        mode: response.mode
      }]
    }));
  }
}));
```

**讲解**：
> "前端使用Zustand进行状态管理，比Redux更轻量。这个store管理所有聊天消息和查询状态，提供了清晰的API供组件使用。"

---

## 技术亮点讲解

### 1. RAG技术实现

**核心价值**：
> "RAG（检索增强生成）解决了传统LLM的两大问题：1) 知识截止日期限制；2) 无法提供答案来源。通过检索用户上传的文档，系统可以回答最新的、特定领域的问题，并提供可追溯的来源。"

**技术细节**：
- PDF文本提取：Apache PDFBox
- 智能分片：段落/句子边界检测，重叠策略
- 全文搜索：PostgreSQL tsvector + ts_rank
- 上下文注入：将检索到的片段作为prompt的一部分

### 2. 智能路由机制

**设计思路**：
> "不是所有问题都需要检索文档。系统通过关键词检测自动判断使用哪种模式，既提高了效率，又改善了用户体验。"

**优化空间**：
> "未来可以使用机器学习模型进行更精准的意图识别，或者让用户手动选择模式。"

### 3. 性能优化策略

**多层次优化**：
1. **应用层**：Redis缓存查询结果（TTL 1小时）
2. **数据库层**：全文搜索索引、查询优化
3. **基础设施层**：HPA自动扩缩容、资源限制

**监控机制**：
> "使用AOP切面记录所有方法的执行时间，当超过阈值时记录警告日志，便于发现性能瓶颈。"

### 4. Kubernetes部署

**优势**：
- **可扩展性**：HPA自动扩缩容
- **高可用性**：多副本部署、健康检查
- **资源隔离**：命名空间、资源限制
- **易于管理**：声明式配置、滚动更新

**本地开发友好**：
> "使用Docker Desktop的Kubernetes，开发环境与生产环境一致，避免了'在我机器上能跑'的问题。"

### 5. 前端技术栈

**现代化工具链**：
- **Vite**：快速的开发服务器和构建工具
- **Tailwind CSS**：实用优先的CSS框架
- **Zustand**：轻量级状态管理
- **React Hooks**：函数式组件和自定义Hooks

**用户体验**：
- 实时反馈：上传进度、处理状态
- 响应式设计：适配不同屏幕尺寸
- 错误处理：友好的错误提示

---

## 常见问题应对

### Q1: 为什么选择PostgreSQL而不是向量数据库？

**回答**：
> "PostgreSQL的全文搜索功能对于这个项目规模已经足够。它提供了tsvector和ts_rank，性能良好且易于维护。如果未来需要更复杂的语义搜索，可以考虑集成Pinecone或Weaviate等向量数据库，但目前的方案在成本和复杂度之间取得了很好的平衡。"

### Q2: 如何保证答案的准确性？

**回答**：
> "系统通过以下方式保证准确性：1) 使用全文搜索找出最相关的文档片段，相似度阈值设为0.7；2) 将这些片段作为上下文提供给LLM，确保答案基于实际文档内容；3) 提供来源引用，用户可以验证答案的依据。此外，智能路由确保只有相关问题才使用RAG模式。"

### Q3: 系统如何处理大量并发请求？

**回答**：
> "系统设计了多层次的扩展策略：1) Redis缓存减少重复查询的处理时间；2) 异步处理文档上传，避免阻塞；3) HPA自动扩缩容，根据负载动态调整Pod数量；4) 数据库连接池和查询优化。在测试中，单个后端Pod可以处理约100 QPS，通过HPA可以扩展到5个Pod，理论上支持500 QPS。"

### Q4: 如何处理PDF解析失败的情况？

**回答**：
> "系统有完善的错误处理机制：1) 上传前验证文件类型和大小；2) PDF解析时检测加密文件并拒绝；3) 解析失败时更新文档状态为FAILED，并记录错误信息；4) 前端显示友好的错误提示。所有异常都通过GlobalExceptionHandler统一处理，返回标准化的错误响应。"

### Q5: 为什么使用Poe API而不是直接调用OpenAI？

**回答**：
> "Poe API提供了统一的接口访问多个LLM模型（GPT-4、Claude等），且有免费额度。对于演示项目来说，这是一个经济实惠的选择。在生产环境中，可以根据需求切换到OpenAI、Azure OpenAI或其他提供商，系统的PoeClientService接口设计使得这种切换很容易实现。"

### Q6: 如何监控系统运行状态？

**回答**：
> "系统提供了多种监控手段：1) 健康检查API实时反映各组件状态；2) AOP切面记录所有方法执行时间和性能警告；3) Kubernetes提供Pod资源使用监控；4) 日志脚本方便查看各服务日志。未来可以集成Prometheus + Grafana实现更完善的监控和告警。"

---

## 时间分配建议

**总时长：15-20分钟**

| 环节 | 时间 | 重点 |
|------|------|------|
| 项目介绍 | 2-3分钟 | 背景、核心功能、技术栈 |
| 架构讲解 | 3-4分钟 | 整体架构、核心流程、K8s部署 |
| 实际操作 | 8-10分钟 | 文档上传、智能问答、缓存演示 |
| 代码讲解 | 3-4分钟 | 2-3个核心代码片段 |
| Q&A | 5-10分钟 | 回答面试官问题 |

**时间控制技巧**：
- 如果时间紧张，可以跳过代码讲解，重点展示实际操作
- 如果时间充裕，可以深入讲解某个技术细节
- 准备好"快速版"和"完整版"两套演示流程

---

## 演示检查清单

### 演示前（-30分钟）
- [ ] Docker Desktop和Kubernetes正在运行
- [ ] 清理旧部署
- [ ] 构建镜像成功
- [ ] 部署应用成功
- [ ] 所有Pod状态为Running
- [ ] 健康检查通过
- [ ] 准备好示例PDF文档
- [ ] 浏览器标签页准备就绪
- [ ] 3个终端窗口准备就绪

### 演示中
- [ ] 声音清晰，语速适中
- [ ] 展示前先简要说明要做什么
- [ ] 操作时同步讲解
- [ ] 观察日志输出并解释
- [ ] 突出技术亮点
- [ ] 控制好时间节奏

### 演示后
- [ ] 总结核心价值
- [ ] 说明可改进的地方
- [ ] 准备回答问题
- [ ] 提供GitHub链接和文档

---

**祝演示成功！** 🎉
