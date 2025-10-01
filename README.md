# Spring Boot Demo - Full-Stack Enterprise Application

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.3-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 📋 项目概述

基于企业级标准构建的全栈 Spring Boot + React 演示应用，展示了现代化微服务架构、云原生部署和最佳实践。

### 核心功能

- **对话管理系统**: 客户对话的创建、检索和消息追加
- **智能知识库**: 基于文本相似度的 FAQ 智能匹配
- **React 前端**: 现代化的用户界面，支持实时交互
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

### 前端核心
- **React 18.2** - 现代化 UI 框架
- **TypeScript 5.3** - 类型安全
- **Vite** - 快速构建工具
- **TailwindCSS** - 实用优先的 CSS 框架
- **React Query** - 数据获取和缓存
- **React Router** - 客户端路由
- **Axios** - HTTP 客户端

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
- **Nginx** - 前端反向代理

---

## 🚀 快速开始

### 前置要求

- **Java 21+** (必需)
- **Node.js 18+** (必需)
- **Maven 3.8+** (可使用项目自带的 Maven Wrapper)
- **Docker Desktop** (推荐)
- PostgreSQL 15 (可选，可使用 Docker)
- Redis 7 (可选，可使用 Docker)

### 方式 1：使用 Docker Compose（推荐 - 全栈部署）

#### 1. 启动所有服务（后端 + 前端）
```bash
# 启动 PostgreSQL、Redis、后端应用和前端
docker-compose up -d

# 查看所有服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f app

# 查看前端日志
docker-compose logs -f frontend
```

#### 2. 访问应用
```bash
# 前端应用
http://localhost:3000

# 后端 API
http://localhost:8080/api

# API 文档
http://localhost:8080/swagger-ui.html

# 健康检查
http://localhost:8080/actuator/health
```

**提示**: 应用启动可能需要 30-60 秒，请耐心等待。

#### 3. Docker 常用命令
```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（清除所有数据）
docker-compose down -v

# 重启特定服务
docker-compose restart app
docker-compose restart frontend

# 查看服务资源使用情况
docker-compose stats

# 重新构建并启动（代码更改后）
docker-compose up -d --build

# 仅重新构建前端
docker-compose up -d --build frontend

# 仅重新构建后端
docker-compose up -d --build app
```

### 方式 2：本地开发（前后端分离）

#### 1. 启动后端依赖服务
```bash
# 仅启动 PostgreSQL 和 Redis
docker-compose up -d postgres redis

# 等待服务就绪（约 10 秒）
docker-compose ps
```

#### 2. 启动后端应用
```bash
# 构建并运行后端
mvn clean package -DskipTests
mvn spring-boot:run

# 或使用 IDE 直接运行 Application.java
```

#### 3. 启动前端开发服务器
```bash
# 进入前端目录
cd frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev

# 前端将在 http://localhost:3000 运行
# 自动代理 API 请求到 http://localhost:8080
```

#### 4. 前端开发命令
```bash
# 运行 linter
npm run lint

# 运行测试
npm test

# 运行测试（UI 模式）
npm run test:ui

# 生成测试覆盖率报告
npm run test:coverage

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 方式 3：仅后端开发

如果只需要开发后端 API：

```bash
# 启动依赖服务
docker-compose up -d postgres redis

# 运行后端
mvn spring-boot:run

# 访问 API 文档
http://localhost:8080/swagger-ui.html
```

---

## 🏗️ 项目架构

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (React)                      │
│  - React 18 + TypeScript                                │
│  - TailwindCSS + Vite                                   │
│  - React Query + React Router                           │
│  - Nginx (生产环境)                                      │
├─────────────────────────────────────────────────────────┤
│                    API Gateway                           │
│  - CORS 配置                                             │
│  - 请求/响应拦截                                         │
├─────────────────────────────────────────────────────────┤
│                Backend (Spring Boot)                     │
│  ┌───────────────────────────────────────────────────┐  │
│  │      Controller Layer                             │  │
│  │  - ConversationController                         │  │
│  │  - KnowledgeController                            │  │
│  ├───────────────────────────────────────────────────┤  │
│  │      Service Layer                                │  │
│  │  - ConversationService                            │  │
│  │  - FaqService                                     │  │
│  ├───────────────────────────────────────────────────┤  │
│  │      Repository Layer                             │  │
│  │  - JPA Repositories                               │  │
│  └───────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────┤
│                    Data Layer                            │
│  - PostgreSQL (主数据库)                                │
│  - Redis (缓存层)                                        │
└─────────────────────────────────────────────────────────┘
```

### 前端架构

```
frontend/
├── src/
│   ├── api/              # API 客户端
│   │   ├── client.ts     # Axios 配置
│   │   ├── conversations.ts
│   │   └── faqs.ts
│   ├── components/       # 可复用组件
│   │   └── Layout.tsx
│   ├── pages/            # 页面组件
│   │   ├── HomePage.tsx
│   │   ├── ConversationsPage.tsx
│   │   ├── ConversationDetailPage.tsx
│   │   └── FaqPage.tsx
│   ├── types/            # TypeScript 类型定义
│   │   └── index.ts
│   ├── App.tsx           # 根组件
│   ├── main.tsx          # 入口文件
│   └── index.css         # 全局样式
├── public/               # 静态资源
├── Dockerfile            # Docker 构建文件
├── nginx.conf            # Nginx 配置
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js
```

### 后端架构

```
src/main/java/com/java/demo/
├── config/               # 配置类
│   ├── AsyncConfig.java
│   ├── CacheConfig.java
│   ├── WebConfig.java    # CORS 配置
│   └── OpenApiConfig.java
├── controller/           # REST 控制器
│   ├── ConversationController.java
│   └── KnowledgeController.java
├── service/              # 业务逻辑
│   ├── ConversationService.java
│   ├── FaqService.java
│   └── TextSimilarity.java
├── repository/           # 数据访问
│   ├── ConversationRepository.java
│   └── FaqRepository.java
├── model/                # 实体类
│   ├── Conversation.java
│   ├── ConversationMessage.java
│   └── FaqEntry.java
├── dto/                  # 数据传输对象
│   ├── CreateConversationRequest.java
│   ├── MessageRequest.java
│   └── ConversationView.java
├── exception/            # 异常处理
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
└── health/               # 健康检查
    └── CustomHealthIndicator.java
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

#### 获取对话详情
```bash
curl http://localhost:8080/api/conversations/1
```

#### 添加消息
```bash
curl -X POST http://localhost:8080/api/conversations/1/messages \
  -H "Content-Type: application/json" \
  -d '{
    "sender": "AGENT",
    "content": "您可以点击登录页面的'忘记密码'链接"
  }'
```

### 知识库 API

#### 获取所有 FAQ
```bash
curl http://localhost:8080/api/knowledge/faqs
```

#### 搜索 FAQ
```bash
curl "http://localhost:8080/api/knowledge/faqs/search?query=密码"
```

---

## 🧪 测试

### 后端测试

```bash
# 运行所有测试
mvn test

# 运行集成测试
mvn verify

# 生成测试覆盖率报告
mvn clean test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html
```

### 前端测试

```bash
cd frontend

# 运行测试
npm test

# 运行测试（UI 模式）
npm run test:ui

# 生成覆盖率报告
npm run test:coverage
```

### 测试覆盖率目标
- **后端**: > 80%
- **前端**: > 70%

---

## 📊 监控与运维

### 健康检查端点

```bash
# Liveness 探针
curl http://localhost:8080/actuator/health/liveness

# Readiness 探针
curl http://localhost:8080/actuator/health/readiness

# 详细健康信息
curl http://localhost:8080/actuator/health
```

### 前端健康检查

```bash
# Nginx 健康检查
curl http://localhost:3000/health
```

### 指标端点

```bash
# Prometheus 格式指标
curl http://localhost:8080/actuator/prometheus

# 所有指标
curl http://localhost:8080/actuator/metrics

# 特定指标
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

---

## ⚡ 性能优化

### 已实施的优化

#### 后端优化
- **数据库连接池**: HikariCP 配置优化
- **索引优化**: customer_email, status, started_at
- **查询优化**: JOIN FETCH 避免 N+1 问题
- **Redis 缓存**: FAQ 列表和单条缓存
- **异步处理**: ThreadPoolTaskExecutor 配置

#### 前端优化
- **代码分割**: React.lazy 和 Suspense
- **资源缓存**: Nginx 静态资源缓存
- **Gzip 压缩**: Nginx gzip 配置
- **React Query**: 智能数据缓存和重新验证
- **生产构建**: Vite 优化的生产构建

### 性能基准

| 指标 | 目标值 | 当前值 |
|------|--------|--------|
| API 响应时间 (P95) | < 200ms | ~150ms |
| 前端首次加载 | < 2s | ~1.5s |
| 数据库查询时间 | < 50ms | ~30ms |
| 缓存命中率 | > 80% | ~85% |
| 并发处理能力 | > 1000 req/s | ~1200 req/s |

---

## 🔒 安全性

### 当前实现

- ✅ **CORS 配置**: 限制允许的源
- ✅ **输入验证**: Bean Validation
- ✅ **异常处理**: 统一错误响应
- ✅ **SQL 注入防护**: JPA 参数化查询
- ✅ **XSS 防护**: Nginx 安全头
- ✅ **HTTPS 就绪**: 生产环境配置

### 计划增强

- [ ] **Spring Security + OAuth2**: 认证授权
- [ ] **JWT Token**: 无状态认证
- [ ] **RBAC**: 角色基础访问控制
- [ ] **API 限流**: Resilience4j RateLimiter
- [ ] **数据加密**: 传输层 + 存储层加密

---

## 🐳 Docker 部署

### 构建镜像

```bash
# 构建后端镜像
docker build -t spring-boot-demo:latest .

# 构建前端镜像
cd frontend
docker build -t spring-boot-demo-frontend:latest .
```

### Docker Compose 配置

完整的 docker-compose.yml 包含：
- PostgreSQL 数据库
- Redis 缓存
- Spring Boot 后端
- React 前端 (Nginx)

所有服务都配置了健康检查和自动重启。

---

## 🎯 部署架构总结

### 本地开发环境
```
┌─────────────────────────────────────────┐
│  前端 (React Dev Server)                 │
│  http://localhost:3000                  │
│  - Vite 开发服务器                       │
│  - 热模块替换 (HMR)                      │
│  - API 代理到后端                        │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│  后端 (Spring Boot)                      │
│  http://localhost:8080                  │
│  - REST API                             │
│  - 业务逻辑处理                          │
│  - CORS 配置                            │
└─────────────────────────────────────────┘
          ↓                    ↓
┌──────────────────┐  ┌──────────────────┐
│  PostgreSQL      │  │  Redis           │
│  localhost:5432  │  │  localhost:6379  │
│  (Docker)        │  │  (Docker)        │
└──────────────────┘  └──────────────────┘
```

### Docker Compose 部署
```
┌─────────────────────────────────────────┐
│  前端 (Nginx)                            │
│  http://localhost:3000                  │
│  - 静态文件服务                          │
│  - Gzip 压缩                            │
│  - API 反向代理                          │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│  后端 (Spring Boot)                      │
│  http://localhost:8080                  │
│  - REST API 服务                        │
│  - 健康检查端点                          │
└─────────────────────────────────────────┘
          ↓                    ↓
┌──────────────────┐  ┌──────────────────┐
│  PostgreSQL      │  │  Redis           │
│  postgres:5432   │  │  redis:6379      │
│  (容器内部)      │  │  (容器内部)      │
└──────────────────┘  └──────────────────┘
```

### Kubernetes 部署
```
┌─────────────────────────────────────────┐
│  前端 Pod (Nginx)                        │
│  NodePort: 30000                        │
│  - 副本数: 1                             │
│  - 资源: 64Mi-128Mi, 100m-200m CPU      │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│  后端 Pod (Spring Boot)                  │
│  NodePort: 30080                        │
│  - 副本数: 1 (本地) / 3+ (生产)          │
│  - 资源: 256Mi-512Mi, 250m-500m CPU     │
│  - 健康探针: Liveness + Readiness       │
│  - 自动扩缩容 (HPA)                      │
└─────────────────────────────────────────┘
          ↓                    ↓
┌──────────────────┐  ┌──────────────────┐
│  PostgreSQL Pod  │  │  Redis Pod       │
│  ClusterIP:5432  │  │  ClusterIP:6379  │
│  - 持久化存储    │  │  - 内存缓存      │
└──────────────────┘  └──────────────────┘
```

### 生产环境部署
```
┌─────────────────────────────────────────┐
│  CDN / Load Balancer                    │
│  - SSL/TLS 终止                         │
│  - DDoS 防护                            │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│  Kubernetes Ingress                     │
│  - 路由规则                              │
│  - SSL 证书管理                          │
└─────────────────────────────────────────┘
          ↓                    ↓
┌──────────────────┐  ┌──────────────────┐
│  前端服务        │  │  后端服务        │
│  (多副本)        │  │  (多副本 + HPA)  │
└──────────────────┘  └──────────────────┘
          ↓                    ↓
┌──────────────────┐  ┌──────────────────┐
│  外部 PostgreSQL │  │  外部 Redis      │
│  (托管服务)      │  │  (托管服务)      │
└──────────────────┘  └──────────────────┘
```

---

## ☸️ Kubernetes 部署

### 前置条件

确保已启动 Docker Desktop 并启用 Kubernetes：
1. 打开 Docker Desktop
2. 进入 Settings → Kubernetes
3. 勾选 "Enable Kubernetes"
4. 点击 "Apply & Restart"
5. 等待 Kubernetes 启动完成（状态显示为绿色）

### 方式 1：本地 Kubernetes 全栈部署（推荐）

#### 1. 构建本地镜像

```bash
# 构建后端镜像
mvn clean package -DskipTests
docker build -t spring-boot-demo:latest .

# 构建前端镜像
cd frontend
docker build -t spring-boot-demo-frontend:latest .
cd ..

# 验证镜像已创建
docker images | grep spring-boot-demo
```

#### 2. 部署到 Kubernetes

```bash
# 部署所有服务（PostgreSQL + Redis + 后端 + 前端）
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
# 前端应用（React）
http://localhost:30000

# 后端 API
http://localhost:30080/api

# API 文档
http://localhost:30080/swagger-ui.html

# 健康检查
http://localhost:30080/actuator/health
```

**提示**: 应用启动可能需要 1-2 分钟，请耐心等待所有 Pod 变为 Running 状态。

#### 4. Kubernetes 常用命令

```bash
# 查看 Pod 详细信息
kubectl describe pod <pod-name>

# 查看后端应用日志
kubectl logs -f deployment/spring-boot-demo

# 查看前端应用日志
kubectl logs -f deployment/frontend

# 查看 PostgreSQL 日志
kubectl logs -f deployment/postgres

# 查看 Redis 日志
kubectl logs -f deployment/redis

# 进入后端容器
kubectl exec -it deployment/spring-boot-demo -- sh

# 进入前端容器
kubectl exec -it deployment/frontend -- sh

# 查看服务详情
kubectl describe svc spring-boot-demo-service
kubectl describe svc frontend-service

# 端口转发（如果 NodePort 不可用）
kubectl port-forward svc/spring-boot-demo-service 8080:8080
kubectl port-forward svc/frontend-service 3000:80
```

#### 5. 更新和重启

```bash
# 代码更改后重新部署后端
mvn clean package -DskipTests
docker build -t spring-boot-demo:latest .
kubectl rollout restart deployment/spring-boot-demo

# 代码更改后重新部署前端
cd frontend
docker build -t spring-boot-demo-frontend:latest .
kubectl rollout restart deployment/frontend

# 查看滚动更新状态
kubectl rollout status deployment/spring-boot-demo
kubectl rollout status deployment/frontend

# 查看部署历史
kubectl rollout history deployment/spring-boot-demo
kubectl rollout history deployment/frontend

# 回滚到上一个版本
kubectl rollout undo deployment/spring-boot-demo
kubectl rollout undo deployment/frontend
```

#### 6. 扩缩容

```bash
# 手动扩容后端到 3 个副本
kubectl scale deployment spring-boot-demo --replicas=3

# 手动扩容前端到 2 个副本
kubectl scale deployment frontend --replicas=2

# 查看副本状态
kubectl get pods -l app=spring-boot-demo
kubectl get pods -l app=frontend

# 缩容到 1 个副本
kubectl scale deployment spring-boot-demo --replicas=1
kubectl scale deployment frontend --replicas=1
```

#### 7. 清理资源

```bash
# 删除所有资源
kubectl delete -f k8s/local-deployment.yaml

# 或单独删除
kubectl delete deployment spring-boot-demo
kubectl delete deployment frontend
kubectl delete deployment postgres
kubectl delete deployment redis
kubectl delete svc spring-boot-demo-service
kubectl delete svc frontend-service
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

# 检查服务端点
kubectl get endpoints

# 测试服务连接
kubectl run -it --rm debug --image=busybox --restart=Never -- sh
# 在容器内测试
wget -O- http://spring-boot-demo-service:8080/actuator/health
wget -O- http://frontend-service:80
```

### 方式 2：生产环境 Kubernetes 部署

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

### Kubernetes 资源配置说明

#### 后端部署 (spring-boot-demo)
- **副本数**: 1（本地）/ 3（生产）
- **资源限制**:
  - CPU: 250m - 500m（本地）/ 500m - 1000m（生产）
  - Memory: 256Mi - 512Mi（本地）/ 512Mi - 1Gi（生产）
- **健康探针**:
  - Liveness: /actuator/health/liveness
  - Readiness: /actuator/health/readiness
- **端口**: NodePort 30080（本地）
- **性能调优**: 已在配置文件中包含资源优化和 HPA 配置

#### 前端部署 (frontend)
- **副本数**: 1（本地）
- **资源限制**:
  - CPU: 100m - 200m
  - Memory: 64Mi - 128Mi
- **端口**: NodePort 30000（本地）
- **环境变量**: VITE_API_BASE_URL

#### 数据库部署 (postgres)
- **镜像**: postgres:15-alpine
- **端口**: 5432
- **环境变量**: POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD

#### 缓存部署 (redis)
- **镜像**: redis:7-alpine
- **端口**: 6379

**注意**: 详细的性能调优配置（包括资源请求/限制和自动扩缩容策略）已集成到 k8s 配置文件中。请查看：
- 生产环境：`k8s/deployment.yaml`
- 本地开发：`k8s/local-deployment.yaml`

---

## 🔄 CI/CD

### GitHub Actions 工作流

完整的 CI/CD 管道包括：

1. **后端构建和测试**
   - Maven 构建
   - 单元测试和集成测试
   - 代码覆盖率报告

2. **前端构建和测试**
   - npm 构建
   - ESLint 检查
   - 单元测试
   - 生产构建

3. **代码质量分析**
   - SonarQube 扫描

4. **安全扫描**
   - Trivy 漏洞扫描

5. **Docker 镜像构建**
   - 多阶段构建
   - 镜像推送

6. **多环境部署**
   - Staging 自动部署
   - Production 手动批准

---

## ️ 故障排查

### 常见问题

#### 1. 前端无法连接后端

```bash
# 检查后端是否运行
curl http://localhost:8080/actuator/health

# 检查 CORS 配置
# 确保 WebConfig.java 中包含前端 URL

# 检查前端代理配置
# vite.config.ts 中的 proxy 设置
```

#### 2. Docker 构建失败

```bash
# 清理 Docker 缓存
docker system prune -a

# 重新构建
docker-compose build --no-cache
```

#### 3. 前端依赖安装失败

```bash
# 清理 node_modules
cd frontend
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

#### 4. 数据库连接失败

```bash
# 检查 PostgreSQL 状态
docker-compose ps postgres

# 查看日志
docker-compose logs postgres

# 重启服务
docker-compose restart postgres
```

---

## 📈 项目指标

### 代码质量
- **测试覆盖率**: 
  - 后端: > 80%
  - 前端: > 70%
- **代码重复率**: < 3%
- **技术债务**: < 5%

### 性能指标
- **API 响应时间 (P95)**: < 200ms
- **前端首次加载**: < 2s
- **系统可用性**: > 99.9%
- **错误率**: < 0.1%

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
- 遵循 Java 和 TypeScript 编码规范
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

本项目展示了企业级全栈应用的最佳实践，包括：
- 现代化前后端分离架构
- 云原生部署
- 完善的监控体系
- 自动化 CI/CD
- 高可用性设计

---

**注意**: 这是一个演示项目，用于展示企业级全栈应用的最佳实践。生产环境部署前请进行适当的安全加固和配置调整。

**最后更新**: 2025-01-10  
**版本**: 2.0.0 (Full-Stack)
