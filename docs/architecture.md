# SCB RAG Demo - 系统架构文档

## 1. 系统概述

SCB RAG Demo 是一个基于检索增强生成（RAG）技术的智能问答系统，结合了传统NLP和现代RAG技术，为用户提供准确、可追溯的答案。

### 1.1 核心特性

- **智能路由**：自动选择NLP或RAG模式
- **文档管理**：支持PDF文档上传、解析和分片
- **全文搜索**：基于PostgreSQL的语义搜索
- **缓存优化**：Redis缓存提升查询性能
- **可追溯性**：提供答案来源引用
- **容器化部署**：Docker + Kubernetes
- **自动扩缩容**：基于HPA的动态资源调整

## 2. 技术栈

### 2.1 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Data JPA | 3.2.0 | 数据访问 |
| PostgreSQL | 15 | 关系数据库 + 全文搜索 |
| Redis | 7 | 缓存层 |
| Apache PDFBox | 2.0.30 | PDF解析 |
| Lombok | 1.18.30 | 代码简化 |
| Swagger/OpenAPI | 3.0 | API文档 |

### 2.2 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| React | 18.2.0 | UI框架 |
| Vite | 5.0.0 | 构建工具 |
| Tailwind CSS | 3.4.0 | CSS框架 |
| Zustand | 4.4.7 | 状态管理 |
| Axios | 1.6.2 | HTTP客户端 |
| React Markdown | 9.0.1 | Markdown渲染 |

### 2.3 基础设施

| 技术 | 版本 | 用途 |
|------|------|------|
| Docker | 最新 | 容器化 |
| Kubernetes | 1.28+ | 容器编排 |
| Nginx | 1.25-alpine | Web服务器 + 反向代理 |

### 2.4 外部服务

| 服务 | 用途 |
|------|------|
| Poe API | AI模型服务（Claude-3.5-Sonnet） |

## 3. 系统架构

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                          用户层                                  │
│                     (浏览器/移动端)                              │
└─────────────────────────────────────────────────────────────────┘
                              ↓ HTTP
┌─────────────────────────────────────────────────────────────────┐
│                    Kubernetes 集群                               │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              Namespace: scb-rag-demo                       │ │
│  │                                                            │ │
│  │  ┌──────────────┐         ┌──────────────┐               │ │
│  │  │   Frontend   │         │   Backend    │               │ │
│  │  │  (React+Nginx)│◄───────│ (Spring Boot)│               │ │
│  │  │  NodePort    │         │  ClusterIP   │               │ │
│  │  │  :30000      │         │  :8080       │               │ │
│  │  │  Replicas:2-4│         │  Replicas:2-5│               │ │
│  │  └──────────────┘         └──────────────┘               │ │
│  │         ▲                        │                        │ │
│  │         │                        ▼                        │ │
│  │         │                 ┌──────────────┐               │ │
│  │         │                 │  PostgreSQL  │               │ │
│  │         │                 │  (Database)  │               │ │
│  │         │                 │  ClusterIP   │               │ │
│  │         │                 │  :5432       │               │ │
│  │         │                 │  PVC: 5Gi    │               │ │
│  │         │                 └──────────────┘               │ │
│  │         │                        │                        │ │
│  │         │                        ▼                        │ │
│  │         │                 ┌──────────────┐               │ │
│  │         │                 │    Redis     │               │ │
│  │         │                 │   (Cache)    │               │ │
│  │         │                 │  ClusterIP   │               │ │
│  │         │                 │  :6379       │               │ │
│  │         │                 └──────────────┘               │ │
│  │         │                                                 │ │
│  │  ┌──────────────────────────────────────────────┐       │ │
│  │  │  HPA (Horizontal Pod Autoscaler)             │       │ │
│  │  │  - Backend: 2-5 replicas (CPU 70%, Mem 80%) │       │ │
│  │  │  - Frontend: 2-4 replicas (CPU 70%)         │       │ │
│  │  └──────────────────────────────────────────────┘       │ │
│  │                                                            │ │
│  │  ┌──────────────────────────────────────────────┐       │ │
│  │  │  ConfigMap & Secret                          │       │ │
│  │  │  - Database credentials                      │       │ │
│  │  │  - Redis configuration                       │       │ │
│  │  │  - Poe API key                               │       │ │
│  │  └──────────────────────────────────────────────┘       │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              ↓ HTTPS
┌─────────────────────────────────────────────────────────────────┐
│                        外部服务                                  │
│                      Poe API (AI服务)                           │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 后端分层架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller层                            │
│  - DocumentController  - QueryController                     │
│  - HealthController    - HistoryController                   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                       Service层                              │
│  - DocumentService     - QueryService                        │
│  - RouterService       - PoeClientService                    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                     Repository层                             │
│  - DocumentRepository  - DocumentChunkRepository             │
│  - QueryHistoryRepository                                    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      数据库层                                │
│  - PostgreSQL (文档、片段、历史)                             │
│  - Redis (查询缓存)                                          │
└─────────────────────────────────────────────────────────────┘
```

## 4. 核心模块设计

### 4.1 文档处理流程

```
上传PDF → 验证文件 → 解析文本 → 智能分片 → 存储数据库
   ↓         ↓          ↓          ↓          ↓
 文件检查   大小/类型   PDFBox    段落边界   Document
           验证       提取文本   重叠切分   + Chunks
```

**关键组件：**
- `FileValidator`：文件验证（大小≤10MB、类型PDF、文件名安全）
- `PdfParser`：PDF文本提取（支持加密检测）
- `TextSplitter`：智能分片（段落/句子边界、重叠切分）
- `DocumentService`：文档管理（异步处理）

**分片策略：**
- 块大小：500字符
- 重叠：50字符
- 边界识别：段落、句子
- 索引：自动创建全文搜索索引

### 4.2 查询处理流程

```
用户查询 → 智能路由 → 模式选择 → 执行查询 → 返回结果
   ↓          ↓          ↓          ↓          ↓
 问题文本   关键词检测   NLP/RAG   Poe API   答案+来源
                                  全文搜索
```

**智能路由规则：**
```java
if (包含特定关键词: "你好", "谢谢", "再见" 等) {
    return NLP模式;  // 直接调用Poe API
} else {
    return RAG模式;  // 检索文档 + 生成答案
}
```

**RAG模式流程：**
1. 全文搜索相关文档片段（PostgreSQL tsvector）
2. 按相似度排序（ts_rank）
3. 构建上下文（Top-K片段，默认K=5）
4. 调用Poe API生成答案
5. 返回答案 + 来源引用

### 4.3 缓存策略

```
查询请求 → 检查缓存 → 缓存命中？
   ↓          ↓           ↓
 生成Key    Redis      是：返回缓存
 (SHA-256)  查询       否：执行查询 → 存入缓存
```

**缓存Key生成：**
```java
String cacheKey = SHA256(question + mode + timestamp的小时部分);
```

**缓存配置：**
- TTL：1小时
- 序列化：JSON
- 驱逐策略：LRU
- 最大内存：256MB

## 5. 数据模型

### 5.1 实体关系图

```
┌─────────────────┐
│    Document     │
│─────────────────│
│ id (PK)         │
│ filename        │
│ file_size       │
│ status          │
│ upload_time     │
└─────────────────┘
        │ 1
        │
        │ N
┌─────────────────┐
│ DocumentChunk   │
│─────────────────│
│ id (PK)         │
│ document_id(FK) │
│ content         │
│ chunk_index     │
│ search_vector   │
└─────────────────┘

┌─────────────────┐
│  QueryHistory   │
│─────────────────│
│ id (PK)         │
│ question        │
│ answer          │
│ mode            │
│ query_time      │
│ sources (JSON)  │
└─────────────────┘
```

### 5.2 全文搜索索引

```sql
-- 创建tsvector列
ALTER TABLE document_chunks 
ADD COLUMN search_vector tsvector;

-- 创建GIN索引
CREATE INDEX idx_search_vector 
ON document_chunks 
USING gin(search_vector);

-- 自动更新触发器
CREATE TRIGGER tsvector_update 
BEFORE INSERT OR UPDATE ON document_chunks
FOR EACH ROW EXECUTE FUNCTION
tsvector_update_trigger(search_vector, 'pg_catalog.simple', content);
```

## 6. Kubernetes部署架构

### 6.1 命名空间和资源组织

**命名空间：** `scb-rag-demo`

**资源清单：**
- Deployments: Frontend, Backend, PostgreSQL, Redis
- Services: 4个（各组件对应）
- ConfigMap: 应用配置
- Secret: 敏感信息
- PVC: PostgreSQL数据持久化（5Gi）、Backend文件上传（2Gi）
- HPA: Backend和Frontend自动扩缩容

### 6.2 核心组件配置

#### Frontend (React + Nginx)
```yaml
Deployment:
  Replicas: 2-4 (HPA控制)
  Image: scb-rag-frontend:latest
  Resources:
    Requests: CPU 100m, Memory 128Mi
    Limits: CPU 200m, Memory 256Mi
  Port: 80
  
Service:
  Type: NodePort
  Port: 80
  NodePort: 30000
  
HPA:
  Min: 2, Max: 4
  Target CPU: 70%
```

#### Backend (Spring Boot)
```yaml
Deployment:
  Replicas: 2-5 (HPA控制)
  Image: scb-rag-backend:latest
  Resources:
    Requests: CPU 500m, Memory 512Mi
    Limits: CPU 1000m, Memory 1Gi
  Port: 8080
  Volume: backend-uploads-pvc (2Gi)
  
Service:
  Type: ClusterIP
  Port: 8080
  
HPA:
  Min: 2, Max: 5
  Target CPU: 70%, Memory: 80%
```

#### PostgreSQL
```yaml
Deployment:
  Replicas: 1
  Image: postgres:15-alpine
  Resources:
    Requests: CPU 250m, Memory 256Mi
    Limits: CPU 500m, Memory 512Mi
  Port: 5432
  Volume: postgres-data-pvc (5Gi)
  
Service:
  Type: ClusterIP
  Port: 5432
```

#### Redis
```yaml
Deployment:
  Replicas: 1
  Image: redis:7-alpine
  Resources:
    Requests: CPU 100m, Memory 128Mi
    Limits: CPU 200m, Memory 256Mi
  Port: 6379
  
Service:
  Type: ClusterIP
  Port: 6379
```

### 6.3 服务暴露和访问

| 服务 | 类型 | 内部端口 | 外部端口 | 访问方式 |
|------|------|----------|----------|----------|
| Frontend | NodePort | 80 | 30000 | http://localhost:30000 |
| Backend | ClusterIP | 8080 | - | 内部访问 |
| PostgreSQL | ClusterIP | 5432 | - | 内部访问 |
| Redis | ClusterIP | 6379 | - | 内部访问 |

**API端点：**
- Swagger UI: http://localhost:30000/api/swagger-ui.html
- Health Check: http://localhost:30000/api/health

### 6.4 配置管理

#### ConfigMap (应用配置)
```yaml
DATABASE_HOST: postgres-service
DATABASE_PORT: "5432"
DATABASE_NAME: scb_rag_demo
REDIS_HOST: redis-service
REDIS_PORT: "6379"
POE_API_URL: https://api.poe.com/bot/
POE_BOT_NAME: Claude-3.5-Sonnet
```

#### Secret (敏感信息)
```yaml
DATABASE_USERNAME: <base64>
DATABASE_PASSWORD: <base64>
POE_API_KEY: <base64>
```

### 6.5 持久化存储

**PostgreSQL PVC:**
- 名称: postgres-data-pvc
- 大小: 5Gi
- 访问模式: ReadWriteOnce
- 存储类: standard (Docker Desktop默认)

**Backend Uploads PVC:**
- 名称: backend-uploads-pvc
- 大小: 2Gi
- 访问模式: ReadWriteOnce
- 挂载路径: /app/uploads

### 6.6 自动扩缩容 (HPA)

**Backend HPA:**
```yaml
minReplicas: 2
maxReplicas: 5
metrics:
  - CPU: 70%
  - Memory: 80%
```

**Frontend HPA:**
```yaml
minReplicas: 2
maxReplicas: 4
metrics:
  - CPU: 70%
```

### 6.7 健康检查

**Liveness Probe (存活探针):**
```yaml
httpGet:
  path: /health
  port: 8080
initialDelaySeconds: 60
periodSeconds: 10
```

**Readiness Probe (就绪探针):**
```yaml
httpGet:
  path: /health
  port: 8080
initialDelaySeconds: 30
periodSeconds: 5
```

## 7. 数据流设计

### 7.1 文档上传流程

```
用户 → Frontend → Backend → 验证 → 解析 → 分片 → PostgreSQL
                                ↓
                            文件存储 (PVC)
```

**详细步骤：**
1. 用户通过前端上传PDF文件
2. Frontend发送multipart/form-data到Backend
3. Backend验证文件（大小、类型、文件名）
4. 保存原始文件到PVC (/app/uploads/documents/)
5. 异步解析PDF提取文本
6. 智能分片（500字符/块，50字符重叠）
7. 存储Document和DocumentChunk到PostgreSQL
8. 自动创建全文搜索索引
9. 返回文档ID和状态

### 7.2 查询处理流程

```
用户 → Frontend → Backend → 路由 → NLP/RAG → Poe API → 响应
                     ↓                  ↓
                   Redis缓存      PostgreSQL搜索
```

**详细步骤：**
1. 用户输入问题
2. Frontend发送查询请求
3. Backend检查Redis缓存
4. 如果缓存未命中：
   - 智能路由判断模式
   - RAG模式：全文搜索 → 构建上下文 → 调用Poe API
   - NLP模式：直接调用Poe API
5. 存储查询历史到PostgreSQL
6. 缓存结果到Redis（1小时TTL）
7. 返回答案和来源引用

## 8. 安全设计

### 8.1 数据安全

- **敏感信息**：使用Kubernetes Secret存储（Base64编码）
- **数据库密码**：环境变量注入，不硬编码
- **API密钥**：Secret管理，定期轮换
- **文件验证**：
  - 大小限制：≤10MB
  - 类型检查：仅允许PDF
  - 文件名验证：防止路径遍历攻击

### 8.2 网络安全

- **CORS配置**：限制允许的源
- **Nginx安全头**：
  - X-Frame-Options: DENY
  - X-Content-Type-Options: nosniff
  - X-XSS-Protection: 1; mode=block
- **内部通信**：ClusterIP服务，不对外暴露
- **外部访问**：仅Frontend通过NodePort暴露

### 8.3 容器安全

- **非root用户**：所有容器使用非特权用户运行
- **只读文件系统**：Nginx配置只读（除缓存目录）
- **资源限制**：CPU和内存限制防止资源耗尽
- **健康检查**：liveness和readiness探针确保服务可用
- **镜像安全**：使用官方Alpine基础镜像

### 8.4 应用安全

- **输入验证**：所有用户输入进行验证和清理
- **SQL注入防护**：使用JPA参数化查询
- **XSS防护**：前端输出转义
- **CSRF防护**：Spring Security配置

## 9. 性能优化

### 9.1 后端优化

- **异步处理**：文档处理使用@Async异步任务
- **连接池**：HikariCP数据库连接池
  - 最小连接：5
  - 最大连接：20
  - 连接超时：30秒
- **JVM优化**：
  - 堆内存：-Xms512m -Xmx1g
  - GC：G1GC
- **缓存策略**：
  - Redis缓存查询结果
  - TTL：1小时
  - 缓存命中率监控

### 9.2 前端优化

- **代码分割**：Vite动态导入
- **资源压缩**：Gzip压缩（Nginx）
- **静态资源缓存**：
  - HTML：no-cache
  - JS/CSS：1年缓存
  - 图片：1个月缓存
- **懒加载**：组件按需加载
- **Tree Shaking**：移除未使用代码

### 9.3 数据库优化

- **索引优化**：
  - GIN索引用于全文搜索
  - B-tree索引用于主键和外键
- **查询优化**：
  - 使用ts_rank排序
  - LIMIT限制返回结果
  - 避免N+1查询
- **连接池**：HikariCP配置
- **定期维护**：VACUUM和ANALYZE

### 9.4 网络优化

- **HTTP/2**：Nginx启用HTTP/2
- **Keep-Alive**：保持连接复用
- **压缩**：Gzip压缩文本资源
- **CDN**：静态资源可使用CDN（可选）

## 10. 监控和日志

### 10.1 日志策略

**应用日志 (Logback):**
```yaml
开发环境: DEBUG
生产环境: INFO
日志格式: JSON
滚动策略: 每天或100MB
保留天数: 30天
```

**访问日志 (Nginx):**
```
格式: combined
位置: /var/log/nginx/access.log
轮转: 每天
```

**性能日志 (AOP):**
- 方法执行时间
- 慢查询记录（>1秒）
- 异常堆栈

### 10.2 健康检查

**健康端点 (/health):**
```json
{
  "status": "UP",
  "components": {
    "database": "UP",
    "redis": "UP",
    "diskSpace": "UP"
  }
}
```

**监控指标：**
- Pod状态和重启次数
- CPU和内存使用率
- 请求响应时间
- 错误率
- 缓存命中率

### 10.3 日志查看

使用提供的脚本查看日志：
```bash
python scripts/logs.py
```

选项：
1. Backend应用日志
2. Frontend应用日志
3. PostgreSQL日志
4. Redis日志
5. 所有Pods状态
6. 查看所有日志

## 11. 扩展性设计

### 11.1 水平扩展

- **无状态设计**：Backend和Frontend无状态，支持多副本
- **HPA自动扩缩容**：
  - Backend: 2-5副本（CPU 70%, Memory 80%）
  - Frontend: 2-4副本（CPU 70%）
- **负载均衡**：Kubernetes Service自动负载均衡
- **会话管理**：无会话依赖，任意Pod可处理请求

### 11.2 垂直扩展

- **资源调整**：修改Deployment资源配置
- **数据库扩展**：
  - 增加PostgreSQL资源
  - 考虑读写分离
  - 分片策略（未来）
- **缓存扩展**：
  - 增加Redis内存
  - Redis集群（未来）

### 11.3 功能扩展

- **多租户支持**：命名空间隔离
- **多区域部署**：跨区域复制
- **微服务拆分**：按功能拆分服务
- **消息队列**：异步任务处理（RabbitMQ/Kafka）

## 12. 灾难恢复

### 12.1 数据备份

**PostgreSQL备份：**
```bash
# 手动备份
kubectl exec -n scb-rag-demo postgres-pod -- pg_dump -U postgres scb_rag_demo > backup.sql

# 定期备份（CronJob）
# 每天凌晨2点备份
```

**PVC备份：**
- 使用Velero进行PVC快照
- 定期备份到云存储

**配置备份：**
- Git版本控制
- 所有YAML文件版本化

### 12.2 故障恢复

**Pod故障：**
- Kubernetes自动重启失败Pod
- Liveness探针检测并重启不健康Pod

**数据恢复：**
```bash
# 从备份恢复
kubectl exec -n scb-rag-demo postgres-pod -- psql -U postgres scb_rag_demo < backup.sql
```

**回滚机制：**
```bash
# 回滚到上一版本
kubectl rollout undo deployment/backend -n scb-rag-demo
kubectl rollout undo deployment/frontend -n scb-rag-demo
```

### 12.3 高可用性

- **多副本部署**：Backend和Frontend至少2个副本
- **健康检查**：自动检测和恢复
- **滚动更新**：零停机部署
- **资源预留**：确保节点有足够资源

## 13. 部署和运维

### 13.1 部署流程

```bash
# 1. 构建Docker镜像
python scripts/build.py

# 2. 部署到Kubernetes
python scripts/deploy.py

# 3. 验证部署
kubectl get pods -n scb-rag-demo
kubectl get svc -n scb-rag-demo

# 4. 查看日志
python scripts/logs.py
```

### 13.2 更新流程

```bash
# 1. 修改代码
# 2. 重新构建镜像
python scripts/build.py

# 3. 更新部署（滚动更新）
kubectl set image deployment/backend backend=scb-rag-backend:latest -n scb-rag-demo
kubectl set image deployment/frontend frontend=scb-rag-frontend:latest -n scb-rag-demo

# 4. 监控更新状态
kubectl rollout status deployment/backend -n scb-rag-demo
```

### 13.3 清理资源

```bash
# 完全清理
python scripts/clean.py

# 仅删除Pods（保留数据）
kubectl delete deployment --all -n scb-rag-demo
```

### 13.4 故障排查

**常见问题：**

1. **Pod Pending**
   ```bash
   kubectl describe pod <pod-name> -n scb-rag-demo
   # 检查资源不足、PVC绑定失败等
   ```

2. **Pod CrashLoopBackOff**
   ```bash
   kubectl logs <pod-name> -n scb-rag-demo
   # 检查应用日志、配置错误等
   ```

3. **服务无法访问**
   ```bash
   kubectl get svc -n scb-rag-demo
   # 检查Service配置、端口映射
   ```

4. **数据库连接失败**
   ```bash
   kubectl exec -it <backend-pod> -n scb-rag-demo -- env | grep DATABASE
   # 检查环境变量配置
   ```

## 14. 资源要求

### 14.1 最小配置

**Docker Desktop设置：**
- CPU: 4核
- 内存: 4GB
- 磁盘: 20GB

**Kubernetes资源：**
- 总CPU请求: ~1000m (1核)
- 总内存请求: ~1GB
- 存储: 7GB (PVC)

### 14.2 推荐配置

**Docker Desktop设置：**
- CPU: 6核
- 内存: 8GB
- 磁盘: 50GB

**生产环境：**
- 多节点集群
- 负载均衡器
- 持久化存储（云存储）
- 监控和告警系统

## 15. 未来改进方向

### 15.1 功能增强

- [ ] 支持更多文档格式（Word、Excel、TXT等）
- [ ] 多语言支持（中文、英文等）
- [ ] 用户认证和授权（OAuth2、JWT）
- [ ] 文档版本管理
- [ ] 高级搜索过滤（日期、标签等）
- [ ] 批量文档上传
- [ ] 文档预览功能

### 15.2 技术升级

- [ ] 向量数据库集成（Pinecone、Weaviate、Milvus）
- [ ] 更先进的嵌入模型（OpenAI Embeddings）
- [ ] 流式响应支持（Server-Sent Events）
- [ ] GraphQL API
- [ ] 微服务架构拆分
- [ ] 事件驱动架构（Kafka）

### 15.3 运维增强

- [ ] Prometheus + Grafana监控
- [ ] ELK日志聚合（Elasticsearch + Logstash + Kibana）
- [ ] CI/CD流水线（GitHub Actions、Jenkins）
- [ ] 自动化测试（单元测试、集成测试、E2E测试）
- [ ] 性能测试（JMeter、Gatling）
- [ ] 安全扫描（SonarQube、Trivy）
- [ ] 蓝绿部署/金丝雀发布

### 15.4 AI能力增强

- [ ] 多模型支持（GPT-4、Claude、Gemini）
- [ ] 模型路由优化
- [ ] 上下文窗口管理
- [ ] 对话历史管理
- [ ] 个性化推荐
- [ ] 智能摘要生成

---

**文档版本**：2.0  
**最后更新**：2025-01-09  
**维护者**：SCB RAG Demo Team
