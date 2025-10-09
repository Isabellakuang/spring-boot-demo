# SCB RAG Demo - 技术演示指南
## Standard Chartered DSS Team - Delivery Lead Tech Position

## 目录
1. [项目概述与职位匹配](#项目概述与职位匹配)
2. [技术能力展示](#技术能力展示)
3. [演示准备](#演示准备)
4. [演示流程](#演示流程)
5. [技术深度讲解](#技术深度讲解)
6. [领导力与交付管理](#领导力与交付管理)
7. [问题应对策略](#问题应对策略)

---

## 项目概述与职位匹配

### 项目背景

**SCB RAG Demo** 是一个企业级智能问答系统，展示了如何构建可扩展的数据和AI平台解决方案。该项目完整体现了Standard Chartered DSS团队所需的核心技术能力和交付管理经验。

### 与职位要求的对应关系

| 职位要求 | 项目体现 |
|---------|---------|
| **E2E软件开发经验** | 从需求分析、架构设计、开发实现到部署运维的完整生命周期 |
| **Java + Spring Boot后端** | 使用Spring Boot 3.2构建RESTful微服务架构 |
| **React前端开发** | 使用React 18 + Vite构建现代化SPA应用 |
| **微服务架构** | 前后端分离、服务解耦、独立部署 |
| **分布式计算** | Kubernetes集群部署、HPA自动扩缩容、负载均衡 |
| **Docker + Kubernetes** | 容器化部署、声明式配置、资源管理 |
| **Python开发** | 自动化脚本（构建、部署、日志管理） |
| **数据工程集成** | PostgreSQL全文搜索、Redis缓存、数据管道设计 |
| **交付管理** | 敏捷开发、版本控制、文档化、质量保证 |
| **技术领导力** | 架构决策、最佳实践、代码规范、性能优化 |

### 核心价值主张

> "这个项目展示了我如何将现代技术栈（Spring Boot、React、Kubernetes）与企业级需求（可扩展性、高可用性、安全性）相结合，构建一个生产就绪的数据和AI平台解决方案。"

---

## 技术能力展示

### 1. 全栈开发能力

#### 后端技术栈（Java + Spring Boot）

**核心技术：**
- **Spring Boot 3.2**：企业级应用框架
- **Spring Data JPA**：数据访问层抽象
- **Spring AOP**：横切关注点（日志、性能监控）
- **Spring Async**：异步任务处理
- **PostgreSQL**：关系数据库 + 全文搜索
- **Redis**：分布式缓存
- **Apache PDFBox**：文档处理

**架构亮点：**
```
Controller层 → Service层 → Repository层 → Database层
     ↓            ↓            ↓              ↓
  REST API    业务逻辑    数据访问        持久化
  
横切关注点（AOP）：
- LoggingAspect：统一日志记录
- PerformanceAspect：性能监控和优化
```

**代码质量：**
- 分层架构清晰
- 依赖注入和IoC
- 异常统一处理
- API文档自动生成（Swagger）
- 单元测试和集成测试

#### 前端技术栈（React + Modern Tooling）

**核心技术：**
- **React 18**：组件化UI框架
- **Vite**：下一代前端构建工具
- **Zustand**：轻量级状态管理
- **Tailwind CSS**：实用优先的CSS框架
- **Axios**：HTTP客户端

**架构亮点：**
```
组件层次：
App
├── Layout (Header + Sidebar)
├── ChatPanel (主要交互区)
│   ├── MessageList
│   ├── InputBox
│   └── ModeSelector
├── DocumentsPage (文档管理)
└── HistoryPage (查询历史)

状态管理（Zustand）：
- queryStore：查询状态和消息
- documentStore：文档列表和上传
- uiStore：UI状态（通知、模态框）
```

**用户体验优化：**
- 响应式设计
- 实时反馈
- 错误处理
- 加载状态
- 无障碍访问

### 2. 微服务与分布式系统

#### 微服务架构设计

**服务拆分：**
```
┌─────────────────────────────────────────────┐
│           Kubernetes Cluster                │
│                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │ Frontend │  │ Backend  │  │PostgreSQL│ │
│  │ Service  │→ │ Service  │→ │ Service  │ │
│  └──────────┘  └──────────┘  └──────────┘ │
│       ↓              ↓              ↓      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │  Nginx   │  │Spring Boot│ │ Database │ │
│  │ (静态)   │  │ (API)    │  │ (数据)   │ │
│  └──────────┘  └──────────┘  └──────────┘ │
│                     ↓                       │
│              ┌──────────┐                  │
│              │  Redis   │                  │
│              │ (缓存)   │                  │
│              └──────────┘                  │
└─────────────────────────────────────────────┘
```

**关键特性：**
- **服务独立性**：每个服务独立部署、扩展
- **API网关模式**：Nginx作为反向代理
- **服务发现**：Kubernetes Service自动发现
- **负载均衡**：ClusterIP自动负载均衡
- **健康检查**：Liveness和Readiness探针

#### 分布式计算实现

**水平扩展（HPA）：**
```yaml
Backend HPA:
  minReplicas: 2
  maxReplicas: 5
  metrics:
    - CPU: 70%
    - Memory: 80%
  
Frontend HPA:
  minReplicas: 2
  maxReplicas: 4
  metrics:
    - CPU: 70%
```

**性能优化策略：**
1. **应用层缓存**：Redis缓存查询结果（TTL 1小时）
2. **数据库优化**：全文搜索索引、连接池
3. **异步处理**：文档处理使用@Async
4. **资源限制**：CPU和内存限制防止资源耗尽

### 3. Docker与Kubernetes专业能力

#### 容器化最佳实践

**多阶段构建（Backend）：**
```dockerfile
# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**优化策略：**
- 使用Alpine基础镜像（减小镜像大小）
- 多阶段构建（分离构建和运行环境）
- 层缓存优化（依赖先复制）
- 非root用户运行（安全性）

#### Kubernetes生产级部署

**资源配置示例（Backend）：**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
  namespace: scb-rag-demo
spec:
  replicas: 2
  selector:
    matchLabels:
      app: backend
  template:
    spec:
      containers:
      - name: backend
        image: scb-rag-backend:latest
        resources:
          requests:
            cpu: 500m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1Gi
        livenessProbe:
          httpGet:
            path: /api/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
        volumeMounts:
        - name: uploads
          mountPath: /app/uploads
      volumes:
      - name: uploads
        persistentVolumeClaim:
          claimName: backend-uploads-pvc
```

**Kubernetes核心概念应用：**
- **Namespace**：资源隔离
- **Deployment**：声明式部署
- **Service**：服务发现和负载均衡
- **ConfigMap/Secret**：配置管理
- **PVC**：持久化存储
- **HPA**：自动扩缩容
- **Probes**：健康检查

### 4. Python自动化能力

**部署自动化脚本（deploy.py）：**
```python
def deploy_to_kubernetes():
    """部署应用到Kubernetes集群"""
    steps = [
        ("创建命名空间", "kubectl apply -f k8s/namespace.yaml"),
        ("创建ConfigMap", "kubectl apply -f k8s/configmap.yaml"),
        ("创建Secret", "kubectl apply -f k8s/secret.yaml"),
        ("部署PostgreSQL", "kubectl apply -f k8s/postgres/"),
        ("部署Redis", "kubectl apply -f k8s/redis/"),
        ("部署Backend", "kubectl apply -f k8s/backend/"),
        ("部署Frontend", "kubectl apply -f k8s/frontend/"),
        ("配置HPA", "kubectl apply -f k8s/hpa.yaml"),
    ]
    
    for description, command in steps:
        print(f"\n{description}...")
        run_command(command)
    
    wait_for_pods_ready()
    print_access_info()
```

**自动化工具集：**
- `build.py`：Docker镜像构建
- `deploy.py`：Kubernetes部署
- `clean.py`：资源清理
- `logs.py`：日志查看

---

## 演示准备

### 环境检查清单

**系统要求：**
- [ ] Docker Desktop运行中
- [ ] Kubernetes集群可用（`kubectl cluster-info`）
- [ ] 资源充足（CPU: 4核, Memory: 4GB）
- [ ] 网络连接正常

**部署准备：**
```bash
# 1. 清理旧部署
python scripts/clean.py

# 2. 构建Docker镜像
python scripts/build.py

# 3. 部署到Kubernetes
python scripts/deploy.py

# 4. 验证部署状态
kubectl get all -n scb-rag-demo

# 5. 等待所有Pod就绪
kubectl wait --for=condition=ready pod -l app=backend -n scb-rag-demo --timeout=300s
kubectl wait --for=condition=ready pod -l app=frontend -n scb-rag-demo --timeout=300s

# 6. 健康检查
curl http://localhost:30000/api/health
```

### 演示材料准备

**浏览器标签页：**
1. 应用前端：http://localhost:30000
2. Swagger API文档：http://localhost:30000/api/swagger-ui.html
3. GitHub仓库：https://github.com/yourusername/scb-rag-demo
4. 架构文档：docs/architecture.md

**终端窗口：**
1. **终端1**：命令执行
2. **终端2**：Backend日志监控
   ```bash
   kubectl logs -f -l app=backend -n scb-rag-demo
   ```
3. **终端3**：Kubernetes资源监控
   ```bash
   watch kubectl get pods -n scb-rag-demo
   ```

**示例数据：**
- 准备2-3个PDF文档（技术文档、产品手册等）
- 准备演示问题列表

---

## 演示流程

### 第一部分：项目介绍与职位匹配（3-4分钟）

**开场白：**
> "感谢给我这个机会展示我的技术能力。今天我将演示一个企业级RAG智能问答系统，这个项目完整体现了Standard Chartered DSS团队Delivery Lead职位所需的核心能力：全栈开发、微服务架构、Kubernetes部署和交付管理。"

**项目价值陈述：**
1. **业务价值**：
   - 解决企业知识管理痛点
   - 提供可追溯的AI答案
   - 支持多文档智能检索

2. **技术价值**：
   - 展示现代微服务架构
   - 云原生部署实践
   - 生产级代码质量

3. **团队价值**：
   - 可复用的技术方案
   - 完善的文档和自动化
   - 最佳实践示范

**技术栈快速概览：**
```
后端：Java 17 + Spring Boot 3.2 + PostgreSQL + Redis
前端：React 18 + Vite + Tailwind CSS + Zustand
基础设施：Docker + Kubernetes + Nginx
自动化：Python脚本 + Shell脚本
```

### 第二部分：架构设计讲解（5-6分钟）

#### 2.1 整体架构（2分钟）

**展示架构图并讲解：**

> "系统采用微服务架构，部署在Kubernetes集群上。前端是React SPA应用，通过Nginx提供静态资源服务。后端是Spring Boot RESTful API，处理业务逻辑。数据层包括PostgreSQL用于持久化和全文搜索，Redis用于查询缓存。"

**关键设计决策：**
1. **为什么选择微服务？**
   - 服务独立部署和扩展
   - 技术栈灵活性
   - 故障隔离

2. **为什么选择Kubernetes？**
   - 自动化部署和扩缩容
   - 服务发现和负载均衡
   - 声明式配置管理

3. **为什么选择PostgreSQL？**
   - 强大的全文搜索功能（tsvector）
   - ACID事务保证
   - 成熟稳定

#### 2.2 核心流程讲解（3分钟）

**文档处理流程：**
```
PDF上传 → 文件验证 → 文本提取 → 智能分片 → 存储 → 索引
   ↓         ↓          ↓          ↓        ↓      ↓
前端UI    大小/类型   PDFBox    段落边界  PostgreSQL GIN索引
```

**讲解要点：**
> "文档处理采用异步模式。用户上传PDF后，系统立即返回文档ID，后台异步处理。使用Apache PDFBox提取文本，然后通过智能分片算法切分为500字符的片段，片段间有50字符重叠以保持上下文连贯性。最后存储到PostgreSQL并自动创建全文搜索索引。"

**查询处理流程：**
```
用户问题 → 智能路由 → 模式选择 → 执行查询 → 返回结果
    ↓         ↓          ↓          ↓          ↓
  输入     关键词检测   NLP/RAG   全文搜索   答案+来源
                                  +Poe API
```

**讲解要点：**
> "查询处理的核心是智能路由。系统通过关键词检测自动判断使用NLP模式还是RAG模式。RAG模式下，先在PostgreSQL中进行全文搜索，找出最相关的5个文档片段，然后将这些片段作为上下文发送给Poe API生成答案。所有查询结果都会缓存到Redis，TTL为1小时。"

#### 2.3 Kubernetes部署架构（1分钟）

**展示Kubernetes资源：**
```bash
kubectl get all -n scb-rag-demo
```

**讲解要点：**
> "应用部署在scb-rag-demo命名空间中。Frontend通过NodePort暴露在30000端口，Backend、PostgreSQL和Redis都是ClusterIP服务，仅内部访问。配置了HPA自动扩缩容，Backend可以从2个副本扩展到5个，Frontend从2个扩展到4个。"

### 第三部分：实际操作演示（8-10分钟）

#### 3.1 系统健康检查（1分钟）

```bash
# 查看所有资源
kubectl get all -n scb-rag-demo

# 查看Pod详情
kubectl get pods -n scb-rag-demo -o wide

# 健康检查
curl http://localhost:30000/api/health | jq
```

**讲解：**
> "所有服务都在运行中。健康检查API返回所有组件状态为UP，包括数据库连接、Redis连接和磁盘空间。"

#### 3.2 文档上传演示（2-3分钟）

**步骤1：打开前端界面**
- 访问 http://localhost:30000
- 展示UI布局和功能

**步骤2：上传PDF文档**
- 点击"上传文档"
- 选择示例PDF文件
- 观察上传进度

**同时观察后端日志：**
```bash
kubectl logs -f -l app=backend -n scb-rag-demo
```

**讲解：**
> "后端日志显示了完整的处理流程：文件验证、PDF解析、文本分片、数据库存储。这个文档被分成了12个片段，整个处理过程耗时约1.8秒。注意这是异步处理，不会阻塞用户操作。"

#### 3.3 智能问答演示（3-4分钟）

**场景1：RAG模式查询**
```
问题：什么是微服务架构？请详细解释其优势。
```

**观察点：**
- 查询模式自动切换为"RAG"
- 右侧显示来源引用
- 答案基于文档内容

**讲解：**
> "系统检测到这是技术问题，自动使用RAG模式。全文搜索找到了3个相关片段，相似度分别为0.89、0.82和0.75。答案是基于这些片段生成的，右侧显示了具体的来源引用，包括文档名和相似度。"

**场景2：缓存演示**
- 重复相同问题
- 观察响应时间差异

**后端日志：**
```
[QueryServiceImpl] Cache hit for query: 什么是微服务架构
[QueryServiceImpl] Query processed in 35ms, mode: RAG (cached)
```

**讲解：**
> "第二次查询只用了35毫秒，因为结果已经缓存在Redis中。这大大提升了重复查询的性能。"

**场景3：NLP模式查询**
```
问题：你好，今天天气怎么样？
```

**讲解：**
> "这是通用对话问题，系统自动切换到NLP模式，直接调用Poe API，不检索文档。"

#### 3.4 Kubernetes管理演示（2分钟）

**展示资源使用：**
```bash
kubectl top pods -n scb-rag-demo
```

**讲解：**
> "可以看到各个Pod的CPU和内存使用情况。Backend Pod当前使用约280MB内存和0.15核CPU，远低于配置的限制（1Gi内存，1核CPU）。"

**展示HPA状态：**
```bash
kubectl get hpa -n scb-rag-demo
```

**讲解：**
> "HPA显示当前CPU使用率约20%，远低于70%的扩容阈值。如果负载增加，系统会自动增加Pod副本数。"

**展示滚动更新：**
```bash
kubectl rollout status deployment/backend -n scb-rag-demo
kubectl rollout history deployment/backend -n scb-rag-demo
```

**讲解：**
> "Kubernetes支持零停机滚动更新。可以看到部署历史和当前状态。"

### 第四部分：代码质量展示（3-4分钟）

#### 4.1 后端代码架构（2分钟）

**展示核心Service实现：**

**QueryServiceImpl.java - 查询处理：**
```java
@Service
@Slf4j
public class QueryServiceImpl implements QueryService {
    
    @Autowired
    private RouterService routerService;
    
    @Autowired
    private DocumentChunkRepository chunkRepository;
    
    @Autowired
    private PoeClientService poeClientService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public QueryResponse processQuery(QueryRequest request) {
        // 1. 检查缓存
        String cacheKey = CacheKeyGenerator.generateKey(request);
        QueryResponse cached = getCachedResult(cacheKey);
        if (cached != null) {
            log.info("Cache hit for query: {}", request.getQuestion());
            return cached;
        }
        
        // 2. 智能路由
        QueryMode mode = routerService.determineMode(request.getQuestion());
        
        // 3. 执行查询
        QueryResponse response;
        if (mode == QueryMode.RAG) {
            response = processRagQuery(request);
        } else {
            response = processNlpQuery(request);
        }
        
        // 4. 缓存结果
        cacheResult(cacheKey, response);
        
        // 5. 保存历史
        saveQueryHistory(request, response);
        
        return response;
    }
    
    private QueryResponse processRagQuery(QueryRequest request) {
        // 全文搜索
        List<DocumentChunk> chunks = chunkRepository
            .searchByContent(request.getQuestion(), 5);
        
        // 构建上下文
        String context = buildContext(chunks);
        
        // 调用Poe API
        String answer = poeClientService.generateAnswer(
            request.getQuestion(), context);
        
        // 构建响应
        return QueryResponse.builder()
            .answer(answer)
            .mode(QueryMode.RAG)
            .sources(buildSourceReferences(chunks))
            .build();
    }
}
```

**讲解要点：**
1. **清晰的业务逻辑**：缓存检查 → 路由判断 → 查询执行 → 结果缓存
2. **依赖注入**：使用Spring的@Autowired
3. **异常处理**：统一的异常处理机制
4. **日志记录**：使用Lombok的@Slf4j
5. **代码可读性**：方法命名清晰，职责单一

#### 4.2 前端代码架构（1分钟）

**展示Zustand状态管理：**

**queryStore.js：**
```javascript
import { create } from 'zustand';
import queryService from '../services/queryService';

const useQueryStore = create((set, get) => ({
  messages: [],
  currentMode: 'AUTO',
  isLoading: false,
  
  submitQuery: async (question) => {
    set({ isLoading: true });
    
    try {
      const response = await queryService.submitQuery({
        question,
        mode: get().currentMode
      });
      
      set(state => ({
        messages: [
          ...state.messages,
          { type: 'user', content: question },
          {
            type: 'assistant',
            content: response.answer,
            sources: response.sources,
            mode: response.mode
          }
        ],
        isLoading: false
      }));
    } catch (error) {
      set({ isLoading: false });
      throw error;
    }
  },
  
  clearMessages: () => set({ messages: [] })
}));

export default useQueryStore;
```

**讲解要点：**
1. **状态管理**：使用Zustand简化状态管理
2. **异步处理**：async/await处理API调用
3. **不可变更新**：使用展开运算符
4. **错误处理**：try-catch捕获异常

#### 4.3 测试和文档（1分钟）

**展示Swagger API文档：**
- 访问 http://localhost:30000/api/swagger-ui.html
- 展示API端点和模型定义

**讲解：**
> "所有API都有完整的Swagger文档，包括请求参数、响应格式和错误码。这使得前后端协作更加高效。"

**展示项目文档：**
- README.md：项目概述和快速开始
- docs/architecture.md：详细架构文档
- docs/api-documentation.md：API文档
- docs/deployment-guide.md：部署指南

---

## 技术深度讲解

### 1. 全文搜索实现

**PostgreSQL tsvector技术：**

```sql
-- 创建全文搜索索引
CREATE INDEX idx_search_vector 
ON document_chunks 
USING gin(to_tsvector('simple', content));

-- 全文搜索查询
SELECT 
    c.*,
    ts_rank(
        to_tsvector('simple', c.content),
        plainto_tsquery('simple', :query)
    ) as similarity
FROM document_chunks c
WHERE to_tsvector('simple', c.content) 
      @@ plainto_tsquery('simple', :query)
ORDER BY similarity DESC
LIMIT 5;
```

**技术优势：**
1. **性能**：GIN索引提供O(log n)查询性能
2. **相关性**：ts_rank计算相似度分数
3. **灵活性**：支持词干提取、停用词过滤
4. **成本**：无需额外的向量数据库

**与向量数据库对比：**
| 特性 | PostgreSQL全文搜索 | 向量数据库 |
|------|-------------------|-----------|
| 实现复杂度 | 低 | 中 |
| 查询性能 | 良好 | 优秀 |
| 语义理解 | 基于关键词 | 基于语义 |
| 基础设施成本 | 低 | 高 |
| 适用场景 | 中小规模 | 大规模 |

### 2. 缓存策略设计

**多层缓存架构：**
```
应用层缓存（Redis）
    ↓
数据库查询缓存
    ↓
PostgreSQL缓冲池
```

**Redis缓存实现：**
```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // 使用Jackson序列化
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        return template;
    }
}
```

**缓存Key生成策略：**
```java
public class CacheKeyGenerator {
    public static String generateKey(QueryRequest request) {
        String raw = request.getQuestion() + 
                     request.getMode() + 
                     getCurrentHour();
        return "query:" + DigestUtils.sha256Hex(raw);
    }
    
    private static String getCurrentHour() {
        return LocalDateTime.now()
            .truncatedTo(ChronoUnit.HOURS)
            .toString();
    }
}
```

**缓存失效策略：**
- TTL：1小时
- 按小时分组：相同问题在同一小时内共享缓存
- LRU驱逐：内存不足时自动清理

### 3. 异步处理设计

**Spring Async配置：**
```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async method {} threw exception", method.getName(), ex);
        };
    }
}
```

**异步文档处理：**
```java
@Service
public class DocumentServiceImpl implements DocumentService {
    
    @Async
    @Override
    public CompletableFuture<Document> processDocument(MultipartFile file) {
        try {
            // 1. 保存文件
            String filePath = saveFile(file);
            
            // 2. 创建文档记录
            Document document = createDocument(file, filePath);
            
            // 3. 解析PDF
            String text = pdfParser.extractText(filePath);
            
            // 4. 分片
            List<String> chunks = textSplitter.split(text);
            
            // 5. 存储片段
            saveChunks(document, chunks);
            
            // 6. 更新状态
            document.setStatus(DocumentStatus.COMPLETED);
            documentRepository.save(document);
            
            return CompletableFuture.completedFuture(document);
        } catch (Exception e) {
            log.error("Failed to process document", e);
            throw new DocumentProcessingException(e);
        }
    }
}
```

**优势：**
- 不阻塞用户请求
- 提高系统吞吐量
- 更好的用户体验

### 4. AOP横切关注点

**性能监控切面：**
```java
@Aspect
@Component
@Slf4j
public class PerformanceAspect {
    
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (executionTime > 1000) {
                log.warn("Slow method execution: {} took {}ms",
                    joinPoint.getSignature(), executionTime);
            } else {
                log.info("{} executed in {}ms",
                    joinPoint.getSignature(), executionTime);
            }
            
            return result;
        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("{} threw exception after {}ms",
                joinPoint.getSignature(), executionTime, e);
            throw e;
        }
    }
}
```

**日志记录切面：**
```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    @Before("execution(* com.scb.ragdemo.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Executing: {} with args: {}",
            joinPoint.getSignature(),
            Arrays.toString(joinPoint.getArgs()));
    }
    
    @AfterReturning(
        pointcut = "execution(* com.scb.ragdemo.service..*(..))",
        returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Completed: {} returned: {}",
            joinPoint.getSignature(),
            result);
    }
}
```

---

## 领导力与交付管理

### 1. 项目管理能力

#### 1.1 敏捷开发实践

**迭代规划：**
```
Sprint 1 (Week 1-2): 基础架构搭建
- 项目初始化
- 数据库设计
- 基础API开发
- Docker容器化

Sprint 2 (Week 3-4): 核心功能开发
- PDF文档处理
- 全文搜索实现
- RAG查询流程
- 前端UI开发

Sprint 3 (Week 5-6): 优化和部署
- 性能优化
- Kubernetes部署
- 自动化脚本
- 文档编写
```

**任务分解示例：**
```
Epic: 文档处理功能
├── Story: PDF上传
│   ├── Task: 文件验证
│   ├── Task: 文件存储
│   └── Task: 前端上传组件
├── Story: PDF解析
│   ├── Task: PDFBox集成
│   ├── Task: 文本提取
│   └── Task: 错误处理
└── Story: 文本分片
    ├── Task: 分片算法
    ├── Task: 数据库存储
    └── Task: 索引创建
```

#### 1.2 质量保证

**代码审查清单：**
- [ ] 代码符合编码规范
- [ ] 单元测试覆盖率 > 80%
- [ ] 无安全漏洞
- [ ] 性能满足要求
- [ ] 文档完整

**测试策略：**
```
单元测试（JUnit + Mockito）
    ↓
集成测试（Spring Boot Test）
    ↓
API测试（Postman/REST Assured）
    ↓
性能测试（JMeter）
    ↓
安全测试（OWASP ZAP）
```

#### 1.3 文档化

**文档体系：**
1. **README.md**：项目概述、快速开始
2. **architecture.md**：详细架构设计
3. **api-documentation.md**：API文档
4. **deployment-guide.md**：部署指南
5. **demo-script.md**：演示脚本
6. **代码注释**：关键逻辑说明

### 2. 技术领导力

#### 2.1 架构决策记录（ADR）

**示例：为什么选择PostgreSQL而不是MongoDB？**

**决策：** 使用PostgreSQL作为主数据库

**理由：**
1. **ACID事务**：确保数据一致性
2. **全文搜索**：内置tsvector支持
3. **关系模型**：文档和片段的关系清晰
4. **成熟稳定**：生产环境验证
5. **团队熟悉度**：降低学习成本

**权衡：**
- 优势：强一致性、丰富功能、低成本
- 劣势：水平扩展相对困难
- 替代方案：MongoDB（文档数据库）、Elasticsearch（搜索引擎）

**结论：** 对于当前项目规模，PostgreSQL是最佳选择

#### 2.2 最佳实践推广

**代码规范：**
```java
// 好的实践：清晰的命名和职责单一
@Service
public class DocumentServiceImpl implements DocumentService {
    
    @Override
    public Document uploadDocument(MultipartFile file) {
        validateFile(file);
        String filePath = saveFile(file);
        return createDocumentRecord(file, filePath);
    }
    
    private void validateFile(MultipartFile file) {
        fileValidator.validate(file);
    }
    
    private String saveFile(MultipartFile file) {
        return fileStorage.save(file);
    }
    
    private Document createDocumentRecord(MultipartFile file, String path) {
        Document document = new Document();
        document.setFilename(file.getOriginalFilename());
        document.setFilePath(path);
        document.setFileSize(file.getSize());
        document.setStatus(DocumentStatus.PROCESSING);
        return documentRepository.save(document);
    }
}
```

**设计模式应用：**
1. **依赖注入**：Spring IoC容器
2. **策略模式**：RouterService（NLP vs RAG）
3. **模板方法**：查询处理流程
4. **工厂模式**：SourceReference构建
5. **单例模式**：Service层Bean

#### 2.3 团队协作

**Git工作流：**
```
main (生产分支)
  ↑
develop (开发分支)
  ↑
feature/xxx (功能分支)
```

**代码审查流程：**
1. 开发者提交Pull Request
2. CI自动运行测试
3. 团队成员代码审查
4. 修改和讨论
5. 合并到develop分支

**知识分享：**
- 技术分享会（每周）
- 文档编写（持续）
- 代码注释（实时）
- Pair Programming（按需）

### 3. 交付管理

#### 3.1 持续集成/持续部署（CI/CD）

**理想的CI/CD流程：**
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test
      - name: Code coverage
        run: mvn jacoco:report
  
  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker images
        run: |
          docker build -t scb-rag-backend:${{ github.sha }} ./backend
          docker build -t scb-rag-frontend:${{ github.sha }} ./frontend
      - name: Push to registry
        run: |
          docker push scb-rag-backend:${{ github.sha }}
          docker push scb-rag-frontend:${{ github.sha }}
  
  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/backend \
            backend=scb-rag-backend:${{ github.sha }}
          kubectl set image deployment/frontend \
            frontend=scb-rag-frontend:${{ github.sha }}
```

#### 3.2 监控和告警

**监控指标：**
1. **应用指标**：
   - 请求响应时间
   - 错误率
   - 吞吐量
   - 缓存命中率

2. **基础设施指标**：
   - CPU使用率
   - 内存使用率
   - 磁盘I/O
   - 网络流量

3. **业务指标**：
   - 文档上传数量
   - 查询次数
   - 用户活跃度

**告警策略：**
```yaml
alerts:
  - name: HighErrorRate
    condition: error_rate > 5%
    duration: 5m
    severity: critical
    
  - name: HighResponseTime
    condition: p95_response_time > 2s
    duration: 5m
    severity: warning
    
  - name: PodCrashLooping
    condition: pod_restart_count > 3
    duration: 10m
    severity: critical
```

#### 3.3 发布管理

**发布策略：**
1. **蓝绿部署**：
   - 维护两套环境（蓝/绿）
   - 新版本部署到绿环境
   - 验证通过后切换流量
   - 保留蓝环境用于快速回滚

2. **金丝雀发布**：
   - 先发布到10%的Pod
   - 监控关键指标
   - 逐步扩大到50%、100%
   - 发现问题立即回滚

3. **滚动更新**（当前使用）：
   - Kubernetes默认策略
   - 逐个替换Pod
   - 零停机部署

**回滚计划：**
```bash
# 查看部署历史
kubectl rollout history deployment/backend -n scb-rag-demo

# 回滚到上一版本
kubectl rollout undo deployment/backend -n scb-rag-demo

# 回滚到特定版本
kubectl rollout undo deployment/backend --to-revision=2 -n scb-rag-demo
```

---

## 问题应对策略

### 技术问题

#### Q1: 为什么选择Spring Boot而不是Quarkus或Micronaut？

**回答：**
> "Spring Boot是我选择的主要原因有三点：
> 
> 1. **生态系统成熟**：Spring Boot拥有最完善的生态系统，包括Spring Data、Spring Security、Spring Cloud等，可以快速集成各种功能。
> 
> 2. **团队熟悉度**：Spring Boot是Java企业开发的事实标准，团队成员普遍熟悉，降低学习成本和维护难度。
> 
> 3. **生产验证**：Spring Boot在大规模生产环境中经过充分验证，稳定性和可靠性有保障。
> 
> 当然，Quarkus在启动速度和内存占用方面有优势，特别适合云原生和无服务器场景。如果项目对启动时间有严格要求，我会考虑Quarkus。Micronaut也是一个不错的选择，特别是在微服务场景下。但对于当前项目，Spring Boot的综合优势更明显。"

#### Q2: 如何保证系统的高可用性？

**回答：**
> "系统的高可用性通过多个层面保证：
> 
> **应用层：**
> - 多副本部署（Backend 2-5个副本，Frontend 2-4个副本）
> - 健康检查（Liveness和Readiness探针）
> - 优雅关闭（处理完当前请求再关闭）
> 
> **数据层：**
> - PostgreSQL主从复制（生产环境）
> - Redis持久化（AOF + RDB）
> - 定期数据备份
> 
> **基础设施层：**
> - Kubernetes自动重启失败Pod
> - HPA自动扩缩容应对负载变化
> - 滚动更新实现零停机部署
> 
> **监控和告警：**
> - 实时监控关键指标
> - 异常自动告警
> - 快速故障定位和恢复
> 
> 在生产环境中，我还会考虑：
> - 多可用区部署
> - 数据库读写分离
> - CDN加速静态资源
> - 限流和熔断机制"

#### Q3: 如何处理大规模并发请求？

**回答：**
> "系统设计了多层次的扩展策略：
> 
> **1. 缓存层优化：**
> - Redis缓存查询结果，减少数据库压力
> - 缓存命中率监控和优化
> - 合理的TTL设置（1小时）
> 
> **2. 数据库优化：**
> - 连接池配置（HikariCP，最大20个连接）
> - 全文搜索索引（GIN索引）
> - 查询优化（LIMIT限制结果集）
> - 读写分离（生产环境）
> 
> **3. 应用层优化：**
> - 异步处理（文档上传不阻塞）
> - 线程池配置（核心5个，最大10个）
> - 资源限制（防止单个请求耗尽资源）
> 
> **4. 基础设施层：**
> - HPA自动扩缩容（CPU 70%触发）
> - 负载均衡（Kubernetes Service）
> - 资源配额（CPU和内存限制）
> 
> **5. 性能测试：**
> 在测试中，单个Backend Pod可以处理约100 QPS。通过HPA扩展到5个Pod，理论上可以支持500 QPS。如果需要更高的并发，可以考虑：
> - 增加Pod副本数上限
> - 使用消息队列（Kafka/RabbitMQ）
> - 引入API网关（Kong/Nginx Plus）
> - 数据库分片"

#### Q4: 如何确保数据安全？

**回答：**
> "数据安全是多层次的：
> 
> **1. 传输安全：**
> - HTTPS加密（生产环境）
> - API认证（JWT Token）
> - CORS配置限制来源
> 
> **2. 存储安全：**
> - 数据库密码加密存储（Kubernetes Secret）
> - 敏感信息不硬编码
> - 定期密钥轮换
> 
> **3. 访问控制：**
> - 基于角色的访问控制（RBAC）
> - API级别的权限验证
> - 审计日志记录
> 
> **4. 输入验证：**
> - 文件类型和大小验证
> - SQL注入防护（参数化查询）
> - XSS防护（输出转义）
> - 文件名安全检查（防止路径遍历）
> 
> **5. 容器安全：**
> - 非root用户运行
> - 只读文件系统
> - 资源限制
> - 镜像安全扫描（Trivy）
> 
> **6. 合规性：**
> - 数据加密（静态和传输）
> - 数据备份和恢复
> - 隐私保护（GDPR合规）"

### 架构问题

#### Q5: 如果要支持百万级文档，你会如何优化架构？

**回答：**
> "百万级文档需要重新考虑架构：
> 
> **1. 数据库层：**
> - **分片策略**：按文档ID范围或哈希分片
> - **读写分离**：主库写入，从库读取
> - **向量数据库**：引入Pinecone/Weavus进行语义搜索
> - **时序数据库**：查询历史存储到InfluxDB
> 
> **2. 搜索层：**
> - **Elasticsearch集群**：替代PostgreSQL全文搜索
> - **分布式索引**：多节点分担搜索压力
> - **搜索优化**：相关性调优、查询缓存
> 
> **3. 缓存层：**
> - **Redis集群**：主从复制 + 哨兵模式
> - **多级缓存**：本地缓存（Caffeine）+ 分布式缓存（Redis）
> - **缓存预热**：热门文档预加载
> 
> **4. 存储层：**
> - **对象存储**：文件存储到S3/MinIO
> - **CDN加速**：静态资源和文档预览
> - **分层存储**：热数据SSD，冷数据HDD
> 
> **5. 计算层：**
> - **微服务拆分**：
>   - 文档服务（上传、解析）
>   - 搜索服务（检索、排序）
>   - 查询服务（RAG、NLP）
>   - 用户服务（认证、授权）
> - **消息队列**：Kafka处理异步任务
> - **流处理**：Flink实时处理
> 
> **6. 监控和运维：**
> - **APM工具**：Datadog/New Relic
> - **日志聚合**：ELK Stack
> - **分布式追踪**：Jaeger/Zipkin
> - **自动化运维**：Ansible/Terraform"

#### Q6: 如何实现多租户支持？

**回答：**
> "多租户架构有几种实现方式：
> 
> **方案1：数据库隔离（推荐用于高安全要求）**
> - 每个租户独立数据库
> - 完全数据隔离
> - 成本较高，管理复杂
> 
> **方案2：Schema隔离（平衡方案）**
> - 共享数据库，独立Schema
> - 较好的隔离性
> - 适中的成本和复杂度
> 
> **方案3：表隔离（推荐用于当前项目）**
> - 共享数据库和Schema
> - 表中添加tenant_id字段
> - 应用层过滤数据
> - 成本最低，易于管理
> 
> **实现细节：**
> ```java
> @Entity
> @Table(name = \"documents\")
> @FilterDef(name = \"tenantFilter\", 
>            parameters = @ParamDef(name = \"tenantId\", type = \"string\"))
> @Filter(name = \"tenantFilter\", 
>         condition = \"tenant_id = :tenantId\")
> public class Document {
>     @Column(name = \"tenant_id\", nullable = false)
>     private String tenantId;
>     // ...
> }
> 
> @Component
> public class TenantContext {
>     private static final ThreadLocal<String> currentTenant = 
>         new ThreadLocal<>();
>     
>     public static void setTenantId(String tenantId) {
>         currentTenant.set(tenantId);
>     }
>     
>     public static String getTenantId() {
>         return currentTenant.get();
>     }
> }
> ```
> 
> **Kubernetes层面：**
> - 每个租户独立Namespace
> - 资源配额限制
> - 网络策略隔离"

### 团队协作问题

#### Q7: 如何处理团队成员之间的技术分歧？

**回答：**
> "技术分歧是正常的，关键是如何建设性地解决：
> 
> **1. 数据驱动决策：**
> - 进行技术调研和POC
> - 收集性能数据和成本分析
> - 基于客观数据而非主观偏好
> 
> **2. 架构决策记录（ADR）：**
> - 记录决策背景和理由
> - 列出考虑的替代方案
> - 说明权衡和取舍
> - 便于后续回顾和调整
> 
> **3. 团队讨论：**
> - 组织技术评审会议
> - 每个方案都有机会陈述
> - 鼓励提问和挑战
> - 最终投票或由架构师决定
> 
> **4. 实践验证：**
> - 小范围试点
> - 收集反馈
> - 必要时调整方案
> 
> **5. 持续学习：**
> - 定期技术分享
> - 鼓励尝试新技术
> - 从失败中学习
> 
> 例如，在这个项目中，关于是否使用向量数据库的讨论：
> - 我们进行了性能测试对比
> - 分析了成本和复杂度
> - 最终决定先使用PostgreSQL全文搜索
> - 但保留了向量数据库的扩展接口
> - 当数据规模增长时可以平滑迁移"

#### Q8: 如何指导初级开发人员？

**回答：**
> "指导初级开发人员需要耐心和方法：
> 
> **1. 任务分配：**
> - 从简单任务开始（如修复小bug）
> - 逐步增加复杂度
> - 提供清晰的需求和验收标准
> 
> **2. 代码审查：**
> - 详细的审查意见
> - 解释为什么而不仅是怎么做
> - 提供改进建议和参考资料
> - 鼓励提问和讨论
> 
> **3. Pair Programming：**
> - 一起解决复杂问题
> - 实时讲解思路和技巧
> - 让他们主导，我提供指导
> 
> **4. 知识分享：**
> - 定期技术分享会
> - 编写详细的文档
> - 推荐学习资源
> - 创建代码示例库
> 
> **5. 反馈机制：**
> - 定期一对一沟通
> - 及时肯定进步
> - 建设性地指出不足
> - 制定成长计划
> 
> **6. 实践机会：**
> - 让他们参与架构讨论
> - 鼓励提出改进建议
> - 给予试错的空间
> - 从错误中学习
> 
> 在这个项目中，我会：
> - 让初级开发人员先实现前端组件
> - 然后逐步接触后端API开发
> - 最后参与架构设计讨论
> - 整个过程中提供持续指导和反馈"

---

## 总结

### 核心竞争力展示

**1. 技术广度：**
- ✅ 全栈开发（Java + React）
- ✅ 微服务架构
- ✅ 容器化和编排（Docker + Kubernetes）
- ✅ 数据库设计和优化
- ✅ 缓存和性能优化
- ✅ 自动化和DevOps

**2. 技术深度：**
- ✅ Spring Boot生态系统
- ✅ PostgreSQL高级特性
- ✅ Kubernetes生产实践
- ✅ 分布式系统设计
- ✅ 性能调优
- ✅ 安全最佳实践

**3. 软技能：**
- ✅ 项目管理和交付
- ✅ 团队协作和沟通
- ✅ 技术领导力
- ✅ 文档编写
- ✅ 问题解决能力
- ✅ 持续学习

### 与DSS团队的契合度

**团队需求 → 我的能力：**

| 团队需求 | 我的展示 |
|---------|---------|
| E2E开发经验 | 完整的项目生命周期 |
| Java + Spring Boot | 生产级代码实现 |
| React前端 | 现代化SPA应用 |
| 微服务架构 | 服务拆分和部署 |
| Kubernetes | 生产级K8s配置 |
| 数据工程 | PostgreSQL + Redis |
| 交付管理 | 自动化和文档化 |
| 技术领导 | 架构决策和最佳实践 |

### 未来发展方向

**短期（1-3个月）：**
- 深入学习Standard Chartered的技术栈
- 熟悉团队工作流程和规范
- 贡献第一个生产特性

**中期（3-6个月）：**
- 领导小型项目
- 指导初级团队成员
- 优化现有系统性能

**长期（6-12个月）：**
- 参与架构设计决策
- 推动技术创新
- 建立最佳实践

---

**演示结束语：**

> "感谢各位的时间。这个项目展示了我在全栈开发、微服务架构和Kubernetes部署方面的能力，也体现了我对代码质量、文档化和交付管理的重视。我相信这些经验和技能能够为Standard Chartered DSS团队带来价值。我期待有机会与团队一起构建企业级数据和AI平台，解决实际业务挑战。谢谢！"

---

**文档版本**：2.0  
**最后更新**：2025-01-09  
**目标职位**：Standard Chartered - Delivery Lead Tech (DSS Team)
