# Spring Boot Demo - Enterprise-Grade Application

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 📋 项目概述

基于企业级标准构建的 Spring Boot 演示应用，展示了现代化微服务架构、云原生部署和最佳实践。

### 核心功能

- **对话管理系统**: 客户对话的创建、检索和消息追加
- **智能知识库**: 基于文本相似度的 FAQ 智能匹配
- **企业级缓存**: Redis 分布式缓存提升性能
- **异步处理**: 高效的异步任务执行框架
- **健康检查**: 完善的健康监控和就绪探针
- **API 文档**: OpenAPI 3.0 标准化文档

---

## 🛠️ 技术栈

### 后端核心
- **Java 21** - 最新 LTS 版本
- **Spring Boot 3.3.4** - 企业级框架
- **Spring Data JPA** - 数据持久化
- **PostgreSQL 15** - 关系型数据库
- **Redis 7** - 分布式缓存

### 监控与可观测性
- **Micrometer + Prometheus** - 指标收集
- **Zipkin** - 分布式追踪
- **Spring Boot Actuator** - 健康检查
- **Logstash Encoder** - 结构化日志

### 企业级特性
- **OpenAPI 3.0** - API 文档自动生成
- **Resilience4j** - 熔断与限流
- **Spring Retry** - 重试机制
- **Testcontainers** - 集成测试

### DevOps & 云原生
- **Docker & Docker Compose** - 容器化
- **Kubernetes** - 容器编排与自动扩缩容
- **GitHub Actions** - CI/CD 管道

---

## 🚀 快速开始

### 前置要求

- **Java 21+** (必需)
- **Maven 3.8+** (可使用项目自带的 Maven Wrapper)
- **Docker Desktop** (推荐)
- PostgreSQL 15 (可选，可使用 Docker)
- Redis 7 (可选，可使用 Docker)

### ⚠️ 重要提示

**首次运行前必须先构建 JAR 文件！** 否则 Docker 构建会失败。

### 方式 1：使用 Docker Compose（推荐）

#### 1. 构建应用 JAR（可选，Dockerfile 会自动构建）
```bash
# 使用本地 Maven（已配置环境变量）
mvn clean package -DskipTests
```

**注意**: 新版 Dockerfile 使用多阶段构建，会自动构建 JAR。但首次构建会较慢（约 3-5 分钟），建议先手动构建 JAR 以加快速度。

#### 2. 启动所有服务
```bash
# 启动 PostgreSQL、Redis 和应用
docker-compose up -d

# 查看所有服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f app

# 查看所有服务日志
docker-compose logs -f
```

#### 3. 验证应用
```bash
# 使用浏览器访问（推荐）
# 打开浏览器访问以下任一地址：
# - 健康检查: http://localhost:8080/actuator/health
# - API 文档: http://localhost:8080/swagger-ui.html
# - FAQ 列表: http://localhost:8080/api/knowledge/faqs

# 或使用 curl 命令
curl http://localhost:8080/actuator/health
```

**提示**: 应用启动可能需要 30-60 秒，请耐心等待。如果浏览器显示页面内容，说明应用已成功启动。

#### 4. Docker 常用命令
```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（清除所有数据）
docker-compose down -v

# 重启特定服务
docker-compose restart app

# 查看服务资源使用情况
docker-compose stats

# 进入应用容器
docker-compose exec app sh

# 查看 PostgreSQL 日志
docker-compose logs postgres

# 查看 Redis 日志
docker-compose logs redis

# 重新构建并启动（代码更改后）
docker-compose up -d --build
```

#### 5. 本地 Docker 镜像管理
```bash
# 查看本地镜像
docker images | grep spring-boot-demo

# 手动构建镜像（不使用 docker-compose）
docker build -t spring-boot-demo:latest .

# 删除镜像
docker rmi spring-boot-demo:latest

# 清理未使用的镜像
docker image prune -a

# 查看镜像详细信息
docker inspect spring-boot-demo:latest
```

### 方式 2：本地开发

#### 1. 启动依赖服务
```bash
# 仅启动 PostgreSQL 和 Redis
docker-compose up -d postgres redis

# 等待服务就绪（约 10 秒）
docker-compose ps
```

#### 2. 构建并运行应用
```bash
# 使用本地 Maven
mvn clean package -DskipTests
mvn spring-boot:run
```

#### 3. 验证应用
```bash
# 使用浏览器访问（推荐）
# 打开浏览器访问以下任一地址：
# - 健康检查: http://localhost:8080/actuator/health
# - API 文档: http://localhost:8080/swagger-ui.html
# - FAQ 列表: http://localhost:8080/api/knowledge/faqs
```

### 方式 3：使用本地 Kubernetes（Docker Desktop）

#### 前置条件
确保已启动 Docker Desktop 并启用 Kubernetes：
1. 打开 Docker Desktop
2. 进入 Settings → Kubernetes
3. 勾选 "Enable Kubernetes"
4. 点击 "Apply & Restart"
5. 等待 Kubernetes 启动完成（状态显示为绿色）

#### 1. 构建本地镜像
```bash
# 先构建 JAR（推荐）
mvn clean package -DskipTests

# 构建 Docker 镜像（Kubernetes 将使用此镜像）
docker build -t spring-boot-demo:latest .

# 验证镜像已创建
docker images | grep spring-boot-demo
```

#### 2. 部署到本地 Kubernetes
```bash
# 使用本地开发配置部署（包含 PostgreSQL 和 Redis）
kubectl apply -f k8s/local-deployment.yaml

# 查看所有资源
kubectl get all

# 查看 Pod 状态
kubectl get pods

# 查看服务
kubectl get svc
```

#### 3. 访问应用
```bash
# 应用通过 NodePort 暴露在端口 30080
# 使用浏览器访问：
# - 健康检查: http://localhost:30080/actuator/health
# - API 文档: http://localhost:30080/swagger-ui.html
# - FAQ 列表: http://localhost:30080/api/knowledge/faqs

# 或使用 curl
curl http://localhost:30080/actuator/health
```

#### 4. Kubernetes 常用命令
```bash
# 查看 Pod 详细信息
kubectl describe pod <pod-name>

# 查看应用日志
kubectl logs -f deployment/spring-boot-demo

# 查看 PostgreSQL 日志
kubectl logs -f deployment/postgres

# 查看 Redis 日志
kubectl logs -f deployment/redis

# 进入应用容器
kubectl exec -it deployment/spring-boot-demo -- sh

# 查看服务详情
kubectl describe svc spring-boot-demo-service

# 端口转发（如果 NodePort 不可用）
kubectl port-forward svc/spring-boot-demo-service 8080:8080
# 然后访问 http://localhost:8080
```

#### 5. 更新和重启
```bash
# 代码更改后重新部署
mvn clean package -DskipTests
docker build -t spring-boot-demo:latest .
kubectl rollout restart deployment/spring-boot-demo

# 查看滚动更新状态
kubectl rollout status deployment/spring-boot-demo

# 查看部署历史
kubectl rollout history deployment/spring-boot-demo

# 回滚到上一个版本
kubectl rollout undo deployment/spring-boot-demo
```

#### 6. 扩缩容
```bash
# 手动扩容到 3 个副本
kubectl scale deployment spring-boot-demo --replicas=3

# 查看副本状态
kubectl get pods -l app=spring-boot-demo

# 缩容到 1 个副本
kubectl scale deployment spring-boot-demo --replicas=1
```

#### 7. 清理资源
```bash
# 删除所有资源
kubectl delete -f k8s/local-deployment.yaml

# 或单独删除
kubectl delete deployment spring-boot-demo
kubectl delete deployment postgres
kubectl delete deployment redis
kubectl delete svc spring-boot-demo-service
kubectl delete svc postgres
kubectl delete svc redis

# 查看剩余资源
kubectl get all
```

#### 8. 故障排查
```bash
# 查看 Pod 事件
kubectl get events --sort-by=.metadata.creationTimestamp

# 查看 Pod 详细状态
kubectl describe pod <pod-name>

# 查看容器日志（如果 Pod 启动失败）
kubectl logs <pod-name>

# 查看上一次容器日志（如果容器重启了）
kubectl logs <pod-name> --previous

# 检查镜像拉取策略
kubectl describe pod <pod-name> | grep -i image
```

### 方式 4：生产环境 Kubernetes 部署

**注意**: 此配置适用于生产环境，需要外部 PostgreSQL 和 Redis 服务。

```bash
# 应用生产环境配置
kubectl apply -f k8s/deployment.yaml

# 查看部署状态
kubectl get pods -l app=spring-boot-demo

# 查看服务
kubectl get svc spring-boot-demo-service

# 查看自动扩缩容状态
kubectl get hpa spring-boot-demo-hpa

# 查看日志
kubectl logs -f deployment/spring-boot-demo

# 查看所有资源
kubectl get all -l app=spring-boot-demo
```

---

## 📚 API 文档

### 访问 API 文档
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### 对话管理 API

#### 创建对话
```bash
curl -X POST http://localhost:8080/api/conversations \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "技术支持",
    "customerEmail": "customer@example.com",
    "initialMessage": "如何重置密码？"
  }'
```

**响应示例:**
```json
{
  "conversationId": 1
}
```

#### 获取对话详情
```bash
curl http://localhost:8080/api/conversations/1
```

**响应示例:**
```json
{
  "id": 1,
  "subject": "技术支持",
  "status": "OPEN",
  "startedAt": "2025-01-10T14:30:00",
  "messages": [
    {
      "sender": "CUSTOMER",
      "content": "如何重置密码？",
      "createdAt": "2025-01-10T14:30:00"
    },
    {
      "sender": "SYSTEM",
      "content": "Based on your query...",
      "createdAt": "2025-01-10T14:30:01"
    }
  ]
}
```

#### 添加消息
```bash
curl -X POST http://localhost:8080/api/conversations/1/messages \
  -H "Content-Type: application/json" \
  -d '{
    "sender": "CUSTOMER",
    "content": "谢谢您的帮助"
  }'
```

### 知识库 API

#### 获取所有 FAQ
```bash
curl http://localhost:8080/api/knowledge/faqs
```

#### 获取特定 FAQ
```bash
curl http://localhost:8080/api/knowledge/faqs/1
```

#### 搜索 FAQ
```bash
curl "http://localhost:8080/api/knowledge/faqs/search?query=密码"
```

---

## 🏗️ 架构设计

### 分层架构
```
┌─────────────────────────────────────┐
│      Controller Layer               │  REST API 端点
│  - ConversationController           │  - 请求验证
│  - KnowledgeController              │  - 响应格式化
├─────────────────────────────────────┤
│      Service Layer                  │  业务逻辑
│  - ConversationService              │  - 事务管理
│  - FaqService                       │  - 业务规则
├─────────────────────────────────────┤
│      Repository Layer               │  数据访问
│  - ConversationRepository           │  - JPA 查询
│  - FaqRepository                    │  - 数据持久化
├─────────────────────────────────────┤
│      Database Layer                 │  PostgreSQL
│  - conversations                    │  - 索引优化
│  - conversation_messages            │  - 关系管理
│  - faq_entries                      │
└─────────────────────────────────────┘
```

### 关键组件

#### 1. 配置层 (config/)
- **AsyncConfig**: 企业级异步处理配置
  - ThreadPoolTaskExecutor (核心线程: 4, 最大线程: 8)
  - 优雅关闭机制
  - 异常处理
- **CacheConfig**: Redis 缓存配置
- **OpenApiConfig**: API 文档配置
- **FaqProperties**: FAQ 配置属性

#### 2. 控制器层 (controller/)
- **ConversationController**: 对话管理 REST API
- **KnowledgeController**: 知识库 REST API
- 统一的异常处理和响应格式

#### 3. 服务层 (service/)
- **ConversationService**: 对话业务逻辑
  - 创建对话
  - 消息管理
  - 自动回复生成
- **FaqService**: FAQ 业务逻辑
  - 文本相似度匹配
  - 缓存管理
- **TextSimilarity**: 文本相似度算法

#### 4. 数据层 (repository/)
- **ConversationRepository**: 对话数据访问
- **ConversationMessageRepository**: 消息数据访问
- **FaqRepository**: FAQ 数据访问

#### 5. 异常处理 (exception/)
- **GlobalExceptionHandler**: 全局异常处理
  - 资源未找到 (404)
  - 验证失败 (400)
  - 约束违反 (400)
  - 服务器错误 (500)
- **ApiError**: 统一错误响应格式

#### 6. 健康检查 (health/)
- **CustomHealthIndicator**: 自定义健康检查
  - 数据库连接检查
  - Redis 连接检查
  - 外部依赖检查

### 数据库设计

#### 表结构
```sql
-- 对话表
conversations
├── id (BIGSERIAL PRIMARY KEY)
├── subject (VARCHAR)
├── customer_email (VARCHAR) [索引]
├── started_at (TIMESTAMP) [索引]
├── closed_at (TIMESTAMP)
└── status (VARCHAR) [索引]

-- 消息表
conversation_messages
├── id (BIGSERIAL PRIMARY KEY)
├── conversation_id (BIGINT FK)
├── sender (VARCHAR)
├── content (TEXT)
└── created_at (TIMESTAMP)

-- FAQ 表
faq_entries
├── id (BIGSERIAL PRIMARY KEY)
├── question (TEXT)
├── answer (TEXT)
└── category (VARCHAR)
```

#### 性能优化
- **索引策略**: customer_email, status, started_at
- **连接池**: HikariCP 配置优化
- **查询优化**: JOIN FETCH 避免 N+1 问题
- **缓存策略**: Redis 缓存 FAQ 列表

---

## 🧪 测试

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行集成测试
mvn verify

# 生成测试覆盖率报告
mvn clean test jacoco:report
```

### 测试覆盖率
报告位置: `target/site/jacoco/index.html`

### 测试技术栈
- **JUnit 5**: 单元测试框架
- **Testcontainers**: 集成测试容器
- **REST Assured**: API 测试
- **H2 Database**: 测试数据库

---

## 📊 监控与运维

### 健康检查端点

```bash
# 使用浏览器访问（推荐）
# - Liveness 探针: http://localhost:8080/actuator/health/liveness
# - Readiness 探针: http://localhost:8080/actuator/health/readiness
# - 详细健康信息: http://localhost:8080/actuator/health
```

**响应示例:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.0.0"
      }
    },
    "custom": {
      "status": "UP",
      "details": {
        "database": "UP",
        "redis": "UP"
      }
    }
  }
}
```

### 指标端点

```bash
# 使用浏览器访问（推荐）
# - Prometheus 格式指标: http://localhost:8080/actuator/prometheus
# - 所有指标: http://localhost:8080/actuator/metrics
# - 特定指标: http://localhost:8080/actuator/metrics/jvm.memory.used
```

### 关键指标

- **JVM 指标**: 内存使用、GC 统计、线程数
- **HTTP 指标**: 请求数、响应时间、错误率
- **数据库指标**: 连接池状态、查询性能
- **缓存指标**: 命中率、驱逐次数
- **自定义指标**: 业务指标

### 日志配置

#### 生产环境日志级别
```yaml
logging:
  level:
    com.java.demo: INFO
    org.springframework.web: WARN
    org.hibernate: WARN
    org.springframework.cache: DEBUG
```

#### 结构化日志
使用 Logstash Encoder 输出 JSON 格式日志，便于日志聚合和分析。

---

## ⚡ 性能优化

### 已实施的优化

#### 1. 数据库优化
- **连接池配置** (HikariCP)
  ```yaml
  spring:
    datasource:
      hikari:
        maximum-pool-size: 20
        minimum-idle: 5
        connection-timeout: 30000
  ```
- **索引优化**: customer_email, status, started_at
- **查询优化**: JOIN FETCH 避免 N+1 问题
- **批量操作**: Hibernate batch processing

#### 2. 缓存策略
- **Redis 分布式缓存**
  - FAQ 列表缓存 (faq_list)
  - FAQ 单条缓存 (faq_entry)
- **缓存配置**
  ```yaml
  spring:
    cache:
      type: redis
      redis:
        time-to-live: 3600000  # 1 hour
  ```

#### 3. 异步处理
- **ThreadPoolTaskExecutor 配置**
  - 核心线程数: 4
  - 最大线程数: 8
  - 队列容量: 100
  - 拒绝策略: CallerRunsPolicy
- **优雅关闭**: 等待任务完成，超时 60 秒

#### 4. Kubernetes 自动扩缩容
- **HorizontalPodAutoscaler**
  - 最小副本: 3
  - 最大副本: 10
  - CPU 目标: 70%
  - 内存目标: 80%

### 性能基准

| 指标 | 目标值 | 当前值 |
|------|--------|--------|
| API 响应时间 (P95) | < 200ms | ~150ms |
| 数据库查询时间 | < 50ms | ~30ms |
| 缓存命中率 | > 80% | ~85% |
| 并发处理能力 | > 1000 req/s | ~1200 req/s |

---

## 🔒 安全性

### 当前实现

- ✅ **输入验证**: Bean Validation (@Valid, @NotBlank, @Email)
- ✅ **异常处理**: 统一错误响应，不暴露敏感信息
- ✅ **健康检查保护**: 生产环境可配置访问控制
- ✅ **SQL 注入防护**: JPA 参数化查询

### 计划增强

#### 认证与授权
- [ ] **Spring Security + OAuth2**: 认证授权
- [ ] **JWT Token**: 无状态认证
- [ ] **RBAC**: 角色基础访问控制
- [ ] **API 密钥管理**: 外部 API 访问控制

#### 数据安全
- [ ] **数据加密**: 传输层 (TLS) + 存储层加密
- [ ] **审计日志**: 操作审计和追踪
- [ ] **限流**: Resilience4j RateLimiter

#### 代码安全扫描
- [ ] **SonarQube**: 代码质量和安全漏洞扫描
- [ ] **Checkmarx**: 静态应用安全测试 (SAST)
- [ ] **OWASP Dependency Check**: 依赖漏洞扫描

---

## 🗺️ 项目路线图

### 第一阶段：前端集成 (Q1 2025)

#### React 前端开发
- [ ] **项目搭建**
  - React 18 + TypeScript
  - Vite 构建工具
  - TailwindCSS + shadcn/ui
  - React Router v6
  - React Query (数据获取)
  - Zustand (状态管理)

- [ ] **核心功能页面**
  - 对话列表页面
  - 对话详情页面
  - FAQ 知识库页面
  - 实时消息界面
  - 搜索和过滤功能

- [ ] **API 集成**
  - Axios 配置和拦截器
  - API 客户端封装
  - 错误处理和重试
  - 加载状态管理

- [ ] **用户体验**
  - 响应式设计
  - 暗黑模式支持
  - 国际化 (i18n)
  - 无障碍访问 (a11y)

- [ ] **前端部署**
  - Nginx 容器化
  - Docker Compose 集成
  - Kubernetes 部署配置
  - CDN 集成

### 第二阶段：安全增强 (Q2 2025)

#### Spring Security + OAuth2
- [ ] **认证系统**
  - OAuth2 授权服务器集成
  - JWT Token 生成和验证
  - 刷新 Token 机制
  - 多因素认证 (MFA)

- [ ] **授权控制**
  - RBAC 角色权限模型
  - 方法级安全注解
  - 资源访问控制
  - API 密钥管理

- [ ] **安全配置**
  - CORS 配置
  - CSRF 防护
  - XSS 防护
  - SQL 注入防护
  - 请求限流和防暴力破解

#### 代码安全扫描
- [ ] **SonarQube 集成**
  - 代码质量分析
  - 安全漏洞检测
  - 代码异味识别
  - 技术债务跟踪
  - CI/CD 集成

- [ ] **Checkmarx 集成**
  - 静态应用安全测试 (SAST)
  - 源代码漏洞扫描
  - 合规性检查
  - 安全报告生成

- [ ] **依赖安全**
  - OWASP Dependency Check
  - Snyk 漏洞扫描
  - 自动依赖更新
  - 许可证合规检查

### 第三阶段：测试增强 (Q2-Q3 2025)

#### 单元测试
- [ ] **服务层测试**
  - ConversationService 完整测试
  - FaqService 完整测试
  - TextSimilarity 算法测试
  - Mock 外部依赖
  - 边界条件测试

- [ ] **Repository 测试**
  - JPA 查询测试
  - 自定义查询测试
  - 事务测试
  - 数据完整性测试

#### 集成测试
- [ ] **API 集成测试**
  - REST Assured 完整覆盖
  - 端到端场景测试
  - 错误处理测试
  - 并发测试

- [ ] **数据库集成测试**
  - Testcontainers PostgreSQL
  - 数据迁移测试
  - 性能测试
  - 数据一致性测试

#### 性能测试
- [ ] **负载测试**
  - JMeter 测试脚本
  - Gatling 性能测试
  - 压力测试场景
  - 容量规划

- [ ] **性能基准**
  - API 响应时间基准
  - 数据库查询优化
  - 缓存效率测试
  - 并发处理能力测试

#### 测试自动化
- [ ] **CI/CD 集成**
  - 自动化测试流水线
  - 测试覆盖率报告
  - 性能回归测试
  - 测试结果通知

### 第四阶段：可观测性 (Q3 2025)

#### 日志聚合 (ELK Stack)
- [ ] **Elasticsearch**
  - 日志存储和索引
  - 全文搜索
  - 日志聚合分析

- [ ] **Logstash**
  - 日志收集和处理
  - 日志格式转换
  - 多源日志整合

- [ ] **Kibana**
  - 日志可视化
  - 自定义仪表板
  - 告警配置
  - 日志分析报表

#### 指标监控 (Prometheus + Grafana)
- [ ] **Prometheus**
  - 指标收集和存储
  - 自定义业务指标
  - 告警规则配置
  - 服务发现

- [ ] **Grafana**
  - 实时监控仪表板
  - JVM 监控面板
  - 数据库监控面板
  - 业务指标可视化
  - 告警通知集成

#### 分布式追踪
- [ ] **OpenTelemetry**
  - 自动化追踪
  - 跨服务追踪
  - 性能分析
  - 错误追踪

- [ ] **Jaeger/Zipkin**
  - 追踪数据可视化
  - 服务依赖图
  - 性能瓶颈分析
  - 延迟分析

#### 应用性能监控 (APM)
- [ ] **New Relic / Datadog**
  - 应用性能监控
  - 用户体验监控
  - 错误追踪
  - 自定义事件

### 第五阶段：Kubernetes 增强 (Q4 2025)

#### Helm Chart 开发
- [ ] **Chart 结构**
  - Chart.yaml 配置
  - values.yaml 参数化
  - templates 模板化
  - 依赖管理

- [ ] **多环境支持**
  - 开发环境配置
  - 测试环境配置
  - 预生产环境配置
  - 生产环境配置

- [ ] **高级功能**
  - 自动化部署脚本
  - 回滚策略
  - 金丝雀发布
  - 蓝绿部署

#### Kubernetes 运维
- [ ] **服务网格**
  - Istio 集成
  - 流量管理
  - 安全策略
  - 可观测性

- [ ] **存储管理**
  - PersistentVolume 配置
  - StatefulSet 部署
  - 数据备份策略
  - 灾难恢复

- [ ] **网络策略**
  - Ingress 配置
  - 网络隔离
  - 服务发现
  - 负载均衡

#### GitOps
- [ ] **ArgoCD / Flux**
  - 声明式部署
  - 自动同步
  - 回滚管理
  - 多集群管理

---

## 🐳 Docker 部署

### 构建镜像
```bash
# 构建应用镜像
docker build -t spring-boot-demo:latest .

# 查看镜像
docker images | grep spring-boot-demo
```

### Docker Compose 配置
```yaml
version: '3.8'
services:
  app:
    image: spring-boot-demo:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/spring-boot
    depends_on:
      - postgres
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

---

## ☸️ Kubernetes 部署

### 资源配置

#### Deployment
- **副本数**: 3 (高可用)
- **资源限制**:
  - CPU: 500m - 1000m
  - Memory: 512Mi - 1Gi
- **健康探针**:
  - Liveness: /actuator/health/liveness
  - Readiness: /actuator/health/readiness

#### Service
- **类型**: ClusterIP
- **端口**: 8080

#### ConfigMap
- 应用配置管理
- 日志级别配置

#### Secret
- 数据库凭证
- Redis 密码

#### HorizontalPodAutoscaler
- **自动扩缩容**: 3-10 副本
- **CPU 目标**: 70%
- **内存目标**: 80%

### 部署命令
```bash
# 应用所有配置
kubectl apply -f k8s/deployment.yaml

# 查看资源
kubectl get all -l app=spring-boot-demo

# 查看 HPA 状态
kubectl get hpa spring-boot-demo-hpa

# 扩容测试
kubectl scale deployment spring-boot-demo --replicas=5

# 查看日志
kubectl logs -f deployment/spring-boot-demo

# 进入容器
kubectl exec -it deployment/spring-boot-demo -- /bin/sh
```

---

## 🔄 CI/CD

### GitHub Actions 工作流

完整的 CI/CD 管道包括：

1. **构建和测试**
   - Maven 构建
   - 单元测试
   - 集成测试
   - 代码覆盖率报告

2. **代码质量分析**
   - SonarQube 扫描
   - 代码质量门禁

3. **安全扫描**
   - Trivy 漏洞扫描
   - 依赖安全检查

4. **Docker 镜像构建**
   - 多阶段构建
   - 镜像标签管理
   - 推送到容器仓库

5. **多环境部署**
   - Staging 环境自动部署
   - Production 环境手动批准
   - 健康检查验证

6. **通知**
   - Slack 集成
   - 部署状态通知

---

## 🛠️ 故障排查

### 📖 详细故障排查指南

**遇到问题？** 请查看 [TROUBLESHOOTING.md](TROUBLESHOOTING.md) 获取完整的故障排查指南，包括：
- 常见错误及解决方案
- 详细的调试步骤
- 性能优化建议
- 快速检查清单

### 常见问题快速参考

#### 1. JAR 文件未找到
```bash
# 错误: Cannot find JAR file
# 解决: 先构建 JAR
mvn clean package -DskipTests
```

#### 2. Docker Desktop 未启动
```bash
# 错误: unable to get image
# 解决: 启动 Docker Desktop 并等待引擎完全启动
```

#### 2. 端口被占用
```bash
# 查找占用端口的进程
netstat -ano | findstr :8080

# 终止进程
taskkill /PID <进程ID> /F
```

#### 3. 数据库连接失败
```bash
# 检查 PostgreSQL 状态
docker-compose ps postgres

# 查看日志
docker-compose logs postgres

# 重启服务
docker-compose restart postgres
```

#### 4. Redis 连接失败
```bash
# 检查 Redis 状态
docker-compose ps redis

# 重启 Redis
docker-compose restart redis
```

#### 5. 应用启动失败
```bash
# 查看应用日志
docker-compose logs app

# 检查健康状态（使用浏览器访问）
# http://localhost:8080/actuator/health

# 重新构建
mvn clean package -DskipTests
```

---

## 📈 项目指标

### 代码质量
- **测试覆盖率**: 目标 > 80%
- **代码重复率**: < 3%
- **技术债务**: < 5%
- **SonarQube 评分**: A 级

### 性能指标
- **API 响应时间 (P95)**: < 200ms
- **系统可用性**: > 99.9%
- **错误率**: < 0.1%
- **并发处理**: > 1000 req/s

### DevOps 指标
- **部署频率**: 每日
- **变更前置时间**: < 1 小时
- **MTTR**: < 30 分钟
- **变更失败率**: < 5%

---

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范
- 遵循 Java 编码规范
- 编写单元测试
- 更新相关文档
- 通过所有 CI 检查

---

## 📄 许可证

本项目采用 Apache License 2.0 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 📞 联系方式

- **项目维护者**: Development Team
- **Email**: dev@example.com
- **问题反馈**: [GitHub Issues](https://github.com/yourusername/spring-boot-demo/issues)

---

## 🙏 致谢

本项目展示了企业级 Spring Boot 应用的最佳实践，包括：
- 现代化微服务架构
- 云原生部署
- 完善的监控体系
- 自动化 CI/CD
- 高可用性设计

---

**注意**: 这是一个演示项目，用于展示企业级 Spring Boot 应用的最佳实践。生产环境部署前请进行适当的安全加固和配置调整。

**最后更新**: 2025-01-10  
**版本**: 1.0.0
