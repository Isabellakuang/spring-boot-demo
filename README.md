# SCB RAG Demo - 智能文档问答系统

基于 RAG (Retrieval-Augmented Generation) 技术的智能文档问答系统，支持 PDF 文档上传、智能查询路由和多模式问答。

## 项目概述

本项目是一个企业级 RAG 演示系统，展示如何构建智能文档问答应用。系统能够：

- 📄 上传和处理 PDF 文档
- 🤖 智能路由查询（NLP 模式 vs RAG 模式）
- 💬 基于文档内容的精准问答
- 🔍 来源追溯和引用展示
- ⚡ 高性能缓存和异步处理

## 技术栈

### 前端
- **框架**: React 18 + Vite
- **UI**: Tailwind CSS
- **状态管理**: React Context API
- **HTTP 客户端**: Axios
- **部署**: Nginx

### 后端
- **框架**: Spring Boot 3.x
- **数据库**: PostgreSQL 15 (全文搜索)
- **缓存**: Redis 7
- **PDF 解析**: Apache PDFBox
- **API 文档**: Springdoc OpenAPI

### 基础设施
- **容器化**: Docker
- **编排**: Kubernetes
- **CI/CD**: GitHub Actions (可选)

## 快速开始

### 前置要求

- Docker Desktop (启用 Kubernetes)
- Java 17+
- Node.js 18+
- Maven 3.8+
- 环境变量: `POE_API_KEY`

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/your-org/scb-rag-demo.git
cd scb-rag-demo
```

2. **配置环境变量**
```bash
cp .env.example .env
# 编辑 .env 文件，设置 POE_API_KEY
```

3. **启动本地开发环境**
```bash
# 使用 Docker Compose
docker-compose up -d

# 或使用 Make 命令
make dev
```

4. **访问应用**
- 前端: http://localhost:3000
- 后端 API: http://localhost:8080
- API 文档: http://localhost:8080/swagger-ui.html

### 部署到 Kubernetes

```bash
# 设置 POE_API_KEY 环境变量
export POE_API_KEY=your_api_key_here

# 执行部署脚本
./scripts/deploy.sh

# 或使用 Make 命令
make deploy
```

## 项目结构

```
scb-rag-demo/
├── frontend/          # React 前端应用
├── backend/           # Spring Boot 后端
├── k8s/              # Kubernetes 配置
├── scripts/          # 部署和工具脚本
├── docs/             # 项目文档
└── sample-data/      # 示例数据
```

详细结构请参考 [架构文档](docs/architecture.md)

## 核心功能

### 1. 文档管理
- PDF 文档上传
- 自动文本提取和分片
- 全文搜索索引
- 文档列表和详情查看

### 2. 智能查询
- **自动模式**: 智能判断使用 NLP 或 RAG
- **NLP 模式**: 直接使用 LLM 回答通用问题
- **RAG 模式**: 基于文档内容的精准问答

### 3. 来源追溯
- 显示答案来源的文档片段
- 相似度评分
- 原文引用

## API 文档

### 文档管理 API

```bash
# 上传文档
POST /api/documents/upload
Content-Type: multipart/form-data

# 获取文档列表
GET /api/documents

# 获取文档详情
GET /api/documents/{id}

# 删除文档
DELETE /api/documents/{id}
```

### 查询 API

```bash
# 智能查询（自动选择模式）
POST /api/query
Content-Type: application/json
{
  "question": "什么是反洗钱？"
}

# 强制 NLP 模式
POST /api/query/nlp

# 强制 RAG 模式
POST /api/query/rag
```

完整 API 文档: [API Documentation](docs/api-documentation.md)

## 开发指南

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

### 后端开发

```bash
cd backend
./mvnw spring-boot:run
```

### 运行测试

```bash
# 前端测试
cd frontend
npm test

# 后端测试
cd backend
./mvnw test
```

## 部署指南

详细部署步骤请参考 [部署指南](docs/deployment-guide.md)

## 演示脚本

面试演示脚本请参考 [演示脚本](docs/demo-script.md)

## 常见问题

### Q: 如何配置 Poe API Key?
A: 在 `.env` 文件中设置 `POE_API_KEY=your_key_here`

### Q: 支持哪些文档格式?
A: 目前仅支持 PDF 格式，未来将支持 Word、TXT 等格式

### Q: 如何调整文档分片大小?
A: 在 `application.yml` 中修改 `rag.chunk-size` 和 `rag.chunk-overlap` 参数

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

## 联系方式

- 项目维护者: [Your Name]
- Email: your.email@example.com
