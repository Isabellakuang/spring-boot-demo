# SCB RAG Demo - Python 脚本使用指南

本目录包含用于管理 SCB RAG Demo 应用的 Python 脚本，适用于跨平台环境。

## 📋 前提条件

- Python 3.7 或更高版本
- Docker Desktop（已启用 Kubernetes）
- kubectl 命令行工具
- POE_API_KEY 环境变量（用于 LLM 服务）

## 🚀 脚本列表

### 1. build.py - 构建 Docker 镜像

构建后端和前端的 Docker 镜像。

**使用方法：**
```bash
python scripts/build.py
```

**功能：**
- 检查 Docker 和 Kubernetes 环境
- 构建后端镜像 (scb-rag-backend:latest)
- 构建前端镜像 (scb-rag-frontend:latest)
- 显示构建结果

---

### 2. deploy.py - 部署到 Kubernetes

将应用部署到 Docker Desktop Kubernetes 集群。

**使用方法：**
```bash
python scripts/deploy.py
```

**功能：**
- 检查 Kubernetes 环境
- 创建命名空间 (scb-rag-demo)
- 部署 ConfigMap 和 Secret
- 部署 PostgreSQL 数据库（包含 PVC）
- 部署 Redis 缓存
- 部署后端应用（包含 uploads PVC）
- 部署前端应用
- 配置 HPA（水平自动扩缩容）
- 显示部署状态和访问信息

**部署顺序：**
1. 命名空间 (scb-rag-demo)
2. 配置（ConfigMap/Secret）
3. PostgreSQL（PVC + Deployment + Service）
4. Redis（Deployment + Service）
5. 后端（uploads PVC + Deployment + Service）
6. 前端（Deployment + Service）
7. HPA（Backend + Frontend）

---

### 3. logs.py - 查看日志

交互式查看各个组件的日志。

**使用方法：**
```bash
python scripts/logs.py
```

**功能：**
- 查看后端应用日志
- 查看前端应用日志
- 查看 PostgreSQL 日志
- 查看 Redis 日志
- 查看所有 Pods 状态
- 查看所有组件日志（最近100行）

**提示：** 按 `Ctrl+C` 退出实时日志查看

### 4. clean.py - 清理资源

清理所有 Kubernetes 资源和 Docker 镜像。

**使用方法：**
```bash
python scripts/clean.py
```

**功能：**
- 删除命名空间 scb-rag-demo（级联删除所有资源）
  - 所有 Pods、Services、Deployments
  - ConfigMaps、Secrets
  - PersistentVolumeClaims（PostgreSQL 数据和上传文件）
  - HPA 配置
- 可选删除 Docker 镜像
- 显示清理结果

**警告：** 此操作会删除所有数据，包括：
- PostgreSQL 数据库中的所有数据
- 上传的 PDF 文档
- Redis 缓存数据

---

## 📝 完整部署流程

### 首次部署

```bash
# 1. 构建镜像
python scripts/build.py

# 2. 部署到 Kubernetes
python scripts/deploy.py

# 3. 查看日志确认部署成功
python scripts/logs.py
```

### 重新部署

```bash
# 1. 清理现有资源
python scripts/clean.py

# 2. 重新构建镜像（如果代码有更改）
python scripts/build.py

# 3. 重新部署
python scripts/deploy.py
```

### 日常维护

```bash
# 查看应用日志
python scripts/logs.py
```

---

---

## 🌐 访问应用

部署成功后，可以通过以下方式访问：

```bash
# 获取服务信息
kubectl get svc -n scb-rag-demo

# 访问地址（NodePort 服务）
# 前端: http://localhost:30000
# 后端 API: http://localhost:30080
# Swagger UI: http://localhost:30080/swagger-ui.html
# API 文档: http://localhost:30080/v3/api-docs
```

**服务端口说明：**
- Frontend Service: NodePort 30000 → 容器端口 80
- Backend Service: NodePort 30080 → 容器端口 8080

---

## 🐛 故障排查

### 检查 Pods 状态
```bash
kubectl get pods -n scb-rag-demo -o wide
```

### 查看 Pod 详细信息
```bash
kubectl describe pod <pod-name> -n scb-rag-demo
```

### 查看实时日志
```bash
# 使用脚本（推荐）
python scripts/logs.py

# 或手动查看
kubectl logs -f <pod-name> -n scb-rag-demo
kubectl logs -f -l app=backend -n scb-rag-demo --tail=100
```

### 进入 Pod 调试
```bash
# Backend
kubectl exec -it deployment/backend -n scb-rag-demo -- /bin/sh

# PostgreSQL
kubectl exec -it deployment/postgres -n scb-rag-demo -- /bin/bash
```

### 检查服务和端点
```bash
kubectl get svc -n scb-rag-demo
kubectl get endpoints -n scb-rag-demo
```

### 检查 PVC 和存储
```bash
kubectl get pvc -n scb-rag-demo
kubectl describe pvc postgres-pvc -n scb-rag-demo
kubectl describe pvc backend-uploads-pvc -n scb-rag-demo
```

### 检查 HPA 状态
```bash
kubectl get hpa -n scb-rag-demo
kubectl describe hpa backend-hpa -n scb-rag-demo
kubectl describe hpa frontend-hpa -n scb-rag-demo
```

### 检查资源使用情况
```bash
kubectl top pods -n scb-rag-demo
kubectl top nodes
```

### 常见问题

**1. Pod 一直处于 Pending 状态**
```bash
# 检查事件
kubectl describe pod <pod-name> -n scb-rag-demo

# 可能原因：
# - 资源不足（CPU/内存）
# - PVC 无法绑定
# - 镜像拉取失败
```

**2. Backend 无法连接数据库**
```bash
# 检查 PostgreSQL 是否就绪
kubectl get pods -l app=postgres -n scb-rag-demo

# 检查 Secret 配置
kubectl get secret scb-rag-secrets -n scb-rag-demo -o yaml

# 查看 Backend 日志
python scripts/logs.py  # 选择选项 1
```

**3. 镜像拉取失败**
```bash
# 确保镜像已构建
docker images | grep scb-rag

# 重新构建镜像
python scripts/build.py
```

**4. HPA 不工作**
```bash
# 检查 metrics-server 是否安装
kubectl get deployment metrics-server -n kube-system

# Docker Desktop 需要手动安装 metrics-server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

---

## 📚 相关文档

- [部署指南](../docs/deployment-guide.md)
- [API 文档](../docs/api-documentation.md)
- [架构说明](../docs/architecture.md)
- [演示脚本](../docs/demo-script.md)

---

## ⚠️ 注意事项

1. **执行顺序**：必须先运行 `python scripts/build.py` 构建镜像，再运行 `python scripts/deploy.py` 部署
2. **环境变量**：部署前必须设置 `POE_API_KEY` 环境变量，否则 Backend 无法启动
3. **清理操作**：`python scripts/clean.py` 会删除所有数据，包括：
   - PostgreSQL 数据库数据
   - 上传的 PDF 文档
   - Redis 缓存
4. **端口占用**：确保以下端口未被占用：
   - 30000（Frontend NodePort）
   - 30080（Backend NodePort）
5. **资源要求**：Docker Desktop 建议配置：
   - 内存：至少 4GB（推荐 8GB）
   - CPU：至少 2 核（推荐 4 核）
   - 磁盘：至少 20GB 可用空间
6. **网络访问**：需要访问以下外部服务：
   - Poe API（https://api.poe.com）
   - Docker Hub（拉取基础镜像）
7. **命名空间**：所有资源部署在 `scb-rag-demo` 命名空间中
8. **持久化存储**：
   - PostgreSQL 数据存储在 `postgres-pvc`
   - 上传文件存储在 `backend-uploads-pvc`
   - 删除 PVC 会永久丢失数据

---

## 💡 提示

- 使用 `python scripts/logs.py` 可以方便地查看各个组件的日志
- 如果遇到问题，先查看日志，通常能找到错误原因
- 开发时可以使用 `docker-compose up -d` 代替 Kubernetes 进行本地测试
- Python 脚本支持跨平台运行（Windows、Linux、macOS）
- 使用 `kubectl get all -n scb-rag-demo` 快速查看所有资源
- HPA 需要 metrics-server，Docker Desktop 可能需要手动安装
- 修改代码后需要重新构建镜像并重新部署

---

## 🔄 可用脚本列表

| Python 脚本 | 说明 |
|------------|------|
| build.py | 构建 Docker 镜像 |
| deploy.py | 部署到 Kubernetes |
| clean.py | 清理所有资源 |
| logs.py | 查看组件日志 |

所有脚本使用 Python 标准库编写，无需额外依赖。
