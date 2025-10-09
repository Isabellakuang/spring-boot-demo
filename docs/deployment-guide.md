# 部署指南

## 目录
1. [环境要求](#环境要求)
2. [配置一致性说明](#配置一致性说明)
3. [快速开始](#快速开始)
4. [本地开发部署](#本地开发部署)
5. [Kubernetes部署](#kubernetes部署)
6. [配置说明](#配置说明)
7. [故障排除](#故障排除)
8. [性能调优](#性能调优)
9. [安全加固](#安全加固)

---

## 配置一致性说明

### 本地与K8s环境对齐

为确保本地开发环境与Kubernetes生产环境的一致性，我们已完成以下配置对齐：

#### ✅ 已完成的改进

1. **统一环境变量命名**
   - 本地和K8s都使用Spring Boot标准环境变量
   - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT`

2. **添加资源限制**
   - Docker Compose现在包含资源限制配置
   - Backend: 768Mi-1536Mi内存, 0.5-1.0 CPU
   - Frontend: 128Mi-256Mi内存, 0.1-0.2 CPU

3. **Redis持久化存储**
   - K8s使用PersistentVolumeClaim替代emptyDir
   - 新增文件: `k8s/redis/redis-pvc.yaml`

4. **数据库自动初始化**
   - K8s添加InitContainer自动执行schema初始化
   - 新增文件: `k8s/postgres/postgres-init-configmap.yaml`

#### 配置对比

| 配置项 | Docker Compose | Kubernetes | 状态 |
|--------|----------------|------------|------|
| 环境变量格式 | Spring Boot标准 | Spring Boot标准 | ✅ 已对齐 |
| 资源限制 | 已配置 | 已配置 | ✅ 已对齐 |
| Redis持久化 | 命名卷 | PVC | ✅ 已对齐 |
| 数据库初始化 | 自动执行 | InitContainer自动执行 | ✅ 已对齐 |
| 镜像版本 | 一致 | 一致 | ✅ 已对齐 |

#### ⚠️ 重要注意事项

1. **本地资源要求**
   - Docker Desktop需分配至少4GB内存和2个CPU核心
   - 现在有资源限制，确保Docker有足够资源

2. **K8s存储类**
   - Redis PVC使用`standard` StorageClass
   - 确认集群有可用的StorageClass: `kubectl get storageclass`

3. **数据库初始化**
   - K8s首次部署自动执行初始化
   - 使用`CREATE TABLE IF NOT EXISTS`，不会覆盖已存在的表
   - 重新初始化：删除Postgres Pod触发InitContainer重新执行

---

## 环境要求

### 硬件要求
- **CPU**: 最低2核，推荐4核
- **内存**: 最低4GB，推荐8GB
- **磁盘**: 最低20GB可用空间

### 软件要求
- **Docker**: 20.10+
- **Docker Desktop**: 包含Kubernetes支持
- **kubectl**: 1.24+
- **Git**: 2.30+
- **Python**: 3.7+（用于运行部署脚本）

### 外部服务
- **Poe API Token**: 从 https://poe.com/api_key 获取

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/scb-rag-demo.git
cd scb-rag-demo
```

### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑.env文件，填入Poe API Token
# POE_API_TOKEN=your_poe_api_token_here
```

### 3. 启动Docker Desktop Kubernetes

1. 打开Docker Desktop
2. 进入Settings → Kubernetes
3. 勾选"Enable Kubernetes"
4. 点击"Apply & Restart"
5. 等待Kubernetes启动完成

### 4. 一键部署

```bash
# 构建镜像
python scripts/build.py

# 部署到Kubernetes
python scripts/deploy.py
```

### 5. 访问应用

- **前端**: http://localhost:30080
- **后端API**: http://localhost:30080/api
- **Swagger文档**: http://localhost:30081/swagger-ui.html

---

## 本地开发部署

### 使用Docker Compose（推荐用于开发）

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

**访问地址**:
- 前端: http://localhost:3000
- 后端: http://localhost:8080
- PostgreSQL: localhost:5432
- Redis: localhost:6379

### 手动启动（用于调试）

#### 1. 启动PostgreSQL

```bash
docker run -d \
  --name postgres \
  -e POSTGRES_DB=ragdemo \
  -e POSTGRES_USER=raguser \
  -e POSTGRES_PASSWORD=ragpass \
  -p 5432:5432 \
  postgres:15-alpine
```

#### 2. 启动Redis

```bash
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7-alpine
```

#### 3. 启动后端

```bash
cd backend

# 使用Maven
./mvnw spring-boot:run

# 或使用已构建的JAR
java -jar target/rag-demo-1.0.0.jar
```

#### 4. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

---

## Kubernetes部署

### 前置检查

```bash
# 检查Kubernetes状态
kubectl cluster-info

# 检查节点状态
kubectl get nodes

# 确认Docker Desktop K8s正在运行
kubectl config current-context
# 应该显示: docker-desktop
```

### 部署步骤

#### 方式1: 使用自动化脚本（推荐）

```bash
# 1. 构建Docker镜像
python scripts/build.py

# 2. 部署到Kubernetes
python scripts/deploy.py

# 3. 查看部署状态
kubectl get all -n scb-rag-demo

# 4. 查看日志
python scripts/logs.py
```

#### 方式2: 手动部署

```bash
# 1. 创建命名空间
kubectl apply -f k8s/namespace.yaml

# 2. 创建ConfigMap和Secret
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml

# 3. 部署PostgreSQL
kubectl apply -f k8s/postgres/

# 4. 部署Redis
kubectl apply -f k8s/redis/

# 5. 等待数据库就绪
kubectl wait --for=condition=ready pod -l app=postgres -n scb-rag-demo --timeout=120s

# 6. 部署后端（包含PVC）
kubectl apply -f k8s/backend/backend-uploads-pvc.yaml
kubectl apply -f k8s/backend/backend-deployment.yaml
kubectl apply -f k8s/backend/backend-service.yaml

# 7. 等待后端就绪
kubectl wait --for=condition=ready pod -l app=backend -n scb-rag-demo --timeout=120s

# 8. 部署前端
kubectl apply -f k8s/frontend/

# 9. 配置HPA（水平自动扩缩容）
kubectl apply -f k8s/hpa.yaml
```

### 验证部署

```bash
# 查看所有资源
kubectl get all -n scb-rag-demo

# 查看Pod状态
kubectl get pods -n scb-rag-demo

# 查看服务
kubectl get svc -n scb-rag-demo

# 查看HPA状态
kubectl get hpa -n scb-rag-demo

# 查看PVC状态
kubectl get pvc -n scb-rag-demo
```

### 访问应用

```bash
# 获取前端访问地址
echo "前端: http://localhost:30080"

# 获取后端API地址
echo "后端API: http://localhost:30080/api"

# 获取Swagger文档地址
echo "Swagger: http://localhost:30081/swagger-ui.html"
```

---

## 配置说明

### 环境变量

在`.env`文件中配置以下变量：

```bash
# Poe API配置
POE_API_TOKEN=your_poe_api_token_here
POE_API_URL=https://api.poe.com/bot/

# 数据库配置
POSTGRES_DB=ragdemo
POSTGRES_USER=raguser
POSTGRES_PASSWORD=ragpass

# Redis配置
REDIS_PASSWORD=redispass

# 应用配置
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

### Kubernetes ConfigMap

编辑`k8s/configmap.yaml`修改应用配置：

```yaml
data:
  # 文档处理配置
  MAX_FILE_SIZE: "10485760"  # 10MB
  CHUNK_SIZE: "1000"
  CHUNK_OVERLAP: "200"
  
  # 缓存配置
  CACHE_TTL: "3600"  # 1小时
  
  # 查询配置
  MAX_SOURCES: "5"
  SIMILARITY_THRESHOLD: "0.7"
```

### 资源限制

在各服务的Deployment中调整资源配置：

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

---

## 故障排除

### 常见问题

#### 1. Pod无法启动

```bash
# 查看Pod详情
kubectl describe pod <pod-name> -n scb-rag-demo

# 查看Pod日志
kubectl logs <pod-name> -n scb-rag-demo

# 查看事件
kubectl get events -n scb-rag-demo --sort-by='.lastTimestamp'
```

**可能原因**:
- 镜像拉取失败：检查镜像是否构建成功
- 资源不足：增加Docker Desktop的资源配置
- 配置错误：检查ConfigMap和Secret

#### 2. 数据库连接失败

```bash
# 检查PostgreSQL状态
kubectl get pod -l app=postgres -n scb-rag-demo

# 查看PostgreSQL日志
kubectl logs -l app=postgres -n scb-rag-demo

# 测试数据库连接
kubectl run -it --rm debug --image=postgres:15-alpine --restart=Never -n scb-rag-demo -- \
  psql -h postgres -U raguser -d ragdemo
```

**解决方案**:
- 确认PostgreSQL Pod正在运行
- 检查数据库凭据是否正确
- 验证Service配置

#### 3. 前端无法访问后端

```bash
# 检查后端服务
kubectl get svc backend -n scb-rag-demo

# 测试后端健康检查
curl http://localhost:30080/api/health

# 查看Nginx配置
kubectl exec -it <frontend-pod> -n scb-rag-demo -- cat /etc/nginx/nginx.conf
```

**解决方案**:
- 检查Nginx代理配置
- 验证后端Service端口
- 查看CORS配置

#### 4. 文件上传失败

**可能原因**:
- 文件大小超过限制（默认10MB）
- 文件类型不支持（仅支持PDF）
- 磁盘空间不足

**解决方案**:
```bash
# 检查PVC状态
kubectl get pvc -n scb-rag-demo

# 查看磁盘使用
kubectl exec -it <backend-pod> -n scb-rag-demo -- df -h

# 增加文件大小限制（修改ConfigMap）
kubectl edit configmap app-config -n scb-rag-demo
```

#### 5. 查询响应慢

```bash
# 查看后端日志中的性能警告
kubectl logs -l app=backend -n scb-rag-demo | grep "Performance"

# 检查Redis缓存
kubectl exec -it <redis-pod> -n scb-rag-demo -- redis-cli INFO stats

# 查看数据库查询性能
kubectl exec -it <postgres-pod> -n scb-rag-demo -- \
  psql -U raguser -d ragdemo -c "SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;"
```

**优化建议**:
- 启用Redis缓存
- 优化数据库索引
- 增加后端副本数

### 日志查看

```bash
# 使用日志脚本（推荐）
python scripts/logs.py

# 手动查看特定服务日志
kubectl logs -l app=backend -n scb-rag-demo --tail=100 -f
kubectl logs -l app=frontend -n scb-rag-demo --tail=100 -f
kubectl logs -l app=postgres -n scb-rag-demo --tail=100 -f
kubectl logs -l app=redis -n scb-rag-demo --tail=100 -f

# 查看所有Pod日志
kubectl logs -n scb-rag-demo --all-containers=true --tail=50
```

### 清理和重新部署

```bash
# 使用清理脚本
python scripts/clean.py

# 重新部署
python scripts/build.py
python scripts/deploy.py

# 或手动清理
kubectl delete namespace scb-rag-demo
docker rmi scb-rag-backend:latest
docker rmi scb-rag-frontend:latest
```

---

## 性能调优

### 1. 数据库优化

```sql
-- 创建额外索引
CREATE INDEX idx_chunks_document_similarity ON document_chunks(document_id, similarity DESC);

-- 分析表统计信息
ANALYZE documents;
ANALYZE document_chunks;
ANALYZE query_history;

-- 查看慢查询
SELECT query, calls, total_time, mean_time 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
```

### 2. Redis缓存优化

```bash
# 增加Redis内存限制
kubectl edit deployment redis -n scb-rag-demo
# 修改: --maxmemory 512mb

# 配置缓存淘汰策略
kubectl exec -it <redis-pod> -n scb-rag-demo -- \
  redis-cli CONFIG SET maxmemory-policy allkeys-lru
```

### 3. 应用层优化

编辑`k8s/configmap.yaml`:

```yaml
data:
  # 增加线程池大小
  ASYNC_CORE_POOL_SIZE: "10"
  ASYNC_MAX_POOL_SIZE: "20"
  
  # 调整缓存TTL
  CACHE_TTL: "7200"  # 2小时
  
  # 优化分片参数
  CHUNK_SIZE: "800"
  CHUNK_OVERLAP: "150"
```

### 4. HPA自动扩缩容

```bash
# 查看HPA状态
kubectl get hpa -n scb-rag-demo

# 调整HPA参数
kubectl edit hpa backend-hpa -n scb-rag-demo
kubectl edit hpa frontend-hpa -n scb-rag-demo

# 手动扩容（临时）
kubectl scale deployment backend --replicas=3 -n scb-rag-demo
```

---

## 安全加固

### 1. 更新密码

```bash
# 生成强密码
openssl rand -base64 32

# 更新Secret
kubectl edit secret app-secret -n scb-rag-demo
# 注意：密码需要base64编码
echo -n "new_password" | base64
```

### 2. 网络策略

创建`k8s/network-policy.yaml`:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: backend-network-policy
  namespace: scb-rag-demo
spec:
  podSelector:
    matchLabels:
      app: backend
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: frontend
    ports:
    - protocol: TCP
      port: 8080
```

### 3. RBAC配置

```bash
# 创建ServiceAccount
kubectl create serviceaccount rag-demo-sa -n scb-rag-demo

# 绑定最小权限Role
kubectl create role pod-reader --verb=get,list --resource=pods -n scb-rag-demo
kubectl create rolebinding pod-reader-binding --role=pod-reader --serviceaccount=scb-rag-demo:rag-demo-sa -n scb-rag-demo
```

### 4. 镜像安全扫描

```bash
# 使用Trivy扫描镜像
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image scb-rag-demo-backend:latest

docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image scb-rag-demo-frontend:latest
```

---

## 监控和维护

### 健康检查

```bash
# 定期检查系统健康
curl http://localhost:30080/api/health
```

### 备份数据库

```bash
# 备份PostgreSQL
kubectl exec -it <postgres-pod> -n scb-rag-demo -- \
  pg_dump -U raguser ragdemo > backup_$(date +%Y%m%d).sql

# 恢复数据库
kubectl exec -i <postgres-pod> -n scb-rag-demo -- \
  psql -U raguser ragdemo < backup_20250104.sql
```

### 更新应用

```bash
# 1. 构建新镜像
python scripts/build.py

# 2. 滚动更新
kubectl rollout restart deployment backend -n scb-rag-demo
kubectl rollout restart deployment frontend -n scb-rag-demo

# 3. 查看更新状态
kubectl rollout status deployment backend -n scb-rag-demo

# 4. 回滚（如果需要）
kubectl rollout undo deployment backend -n scb-rag-demo
```

---

## 附录

### 有用的命令

```bash
# 进入Pod Shell
kubectl exec -it <pod-name> -n scb-rag-demo -- /bin/sh

# 端口转发
kubectl port-forward svc/backend 8080:8080 -n scb-rag-demo

# 复制文件
kubectl cp <pod-name>:/path/to/file ./local-file -n scb-rag-demo

# 查看资源使用
kubectl top pods -n scb-rag-demo
kubectl top nodes
```

### 参考链接

- [Kubernetes官方文档](https://kubernetes.io/docs/)
- [Docker Desktop文档](https://docs.docker.com/desktop/)
- [Spring Boot文档](https://spring.io/projects/spring-boot)
- [React文档](https://react.dev/)
- [PostgreSQL文档](https://www.postgresql.org/docs/)
- [Redis文档](https://redis.io/documentation)

---

**最后更新**: 2025-01-04
