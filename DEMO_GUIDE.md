# Spring Boot Demo 项目详细指南

## 项目概述

这是一个全栈企业级应用演示项目，展示了现代数据平台和AI应用开发的完整技术栈。项目实现了一个智能对话系统，集成了NLP（自然语言处理）和RAG（检索增强生成）功能，完美契合Standard Chartered银行Data Solution Service团队的技术要求。

### 核心价值主张

本项目展示了以下关键能力，与Job Summary中的要求完全对应：

- ✅ **全栈开发能力**：Java Spring Boot后端 + React前端
- ✅ **微服务架构**：多服务协同、容器化部署
- ✅ **数据平台集成**：向量数据库、语义搜索、RAG引擎
- ✅ **AI/LLM应用**：大语言模型集成、智能对话系统
- ✅ **云原生部署**：Docker容器化、Kubernetes编排
- ✅ **企业级最佳实践**：API文档、健康检查、错误处理、日志管理

---

## 技术栈映射（与Job Summary对照）

### Backend - Java技术栈

| Job要求 | 项目实现 | 文件位置 |
|---------|---------|---------|
| Java | Java 21 | `backend/pom.xml` |
| Spring Boot | Spring Boot 3.3.4 | `backend/pom.xml` |
| Microservices | RESTful API服务 | `backend/src/main/java/com/java/demo/controller/` |
| RESTful APIs | 多个REST控制器 | `ConversationController`, `LlmController`, `RagController` |
| Distributed Computing | 服务间通信、异步处理 | `SimplifiedLlmService`, `SimplifiedRagService` |
| Docker | Dockerfile | `backend/Dockerfile` |
| Kubernetes | K8s部署配置 | `infra/k8s/backend-deployment.yaml` |

**核心依赖**：
```xml
- Spring Boot Starter Web (RESTful API)
- Spring Boot Starter Data JPA (数据持久化)
- Spring Boot Starter WebFlux (异步HTTP客户端)
- Spring Boot Starter Actuator (健康检查、监控)
- PostgreSQL Driver (关系型数据库)
- SpringDoc OpenAPI (API文档)
- Resilience4j (容错、重试机制)
```

### Frontend - React技术栈

| Job要求 | 项目实现 | 文件位置 |
|---------|---------|---------|
| React | React 18.2 | `frontend/package.json` |
| JavaScript/TypeScript | TypeScript | `frontend/src/` |
| Modern Frontend | Vite构建工具 | `frontend/vite.config.ts` |
| State Management | Zustand | `frontend/src/core/state/appStore.ts` |
| API Integration | Axios + React Query | `frontend/src/core/api/` |
| UI Framework | Tailwind CSS | `frontend/tailwind.config.js` |

**核心依赖**：
```json
- React 18.2 (UI框架)
- React Router DOM (路由管理)
- @tanstack/react-query (服务端状态管理)
- Axios (HTTP客户端)
- Zustand (客户端状态管理)
- TypeScript (类型安全)
- Tailwind CSS (样式框架)
- Vite (构建工具)
- Vitest (单元测试)
```

### Python - AI/ML服务

| Job要求 | 项目实现 | 文件位置 |
|---------|---------|---------|
| Python | Python 3.x | `python-services/ml-service/` |
| AI/LLM Integration | FastAPI服务 | `python-services/ml-service/app.py` |
| Vector Database | ChromaDB | `python-services/ml-service/vector_store.py` |
| NLP | Sentence Transformers | `python-services/ml-service/rag_engine.py` |

**核心依赖**：
```python
- FastAPI (高性能API框架)
- ChromaDB (向量数据库)
- Sentence Transformers (本地嵌入模型)
- Uvicorn (ASGI服务器)
- Pydantic (数据验证)
```

### DevOps & Infrastructure

| Job要求 | 项目实现 | 文件位置 |
|---------|---------|---------|
| Docker | 多服务容器化 | `*/Dockerfile` |
| Kubernetes | 完整K8s配置 | `infra/k8s/` |
| Cloud Platforms | 云原生架构 | `infra/` |
| DevOps Practices | CI/CD就绪 | `deploy-to-k8s.ps1` |

---

## 项目架构详解

### 1. 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        用户界面层                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  React Frontend (Port 3000)                          │   │
│  │  - TypeScript + Vite                                 │   │
│  │  - React Query (数据获取)                             │   │
│  │  - Zustand (状态管理)                                 │   │
│  │  - Tailwind CSS (样式)                               │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                      应用服务层                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Spring Boot Backend (Port 8080)                     │   │
│  │  - RESTful API Controllers                           │   │
│  │  - Business Logic Services                           │   │
│  │  - JPA Repositories                                  │   │
│  │  - OpenAPI Documentation                             │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                ↓ HTTP                    ↓ JDBC
┌──────────────────────────┐    ┌─────────────────────────────┐
