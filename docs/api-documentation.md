# API 接口文档

## 目录
1. [概述](#概述)
2. [认证](#认证)
3. [通用响应格式](#通用响应格式)
4. [错误码](#错误码)
5. [健康检查API](#健康检查api)
6. [文档管理API](#文档管理api)
7. [查询API](#查询api)
8. [历史记录API](#历史记录api)

---

## 概述

**Base URL**: `http://localhost:30080/api`

**Content-Type**: `application/json`

**API文档**: `http://localhost:30081/swagger-ui.html`

---

## 认证

当前版本暂不需要认证。未来版本将支持JWT Token认证。

---

## 通用响应格式

所有API响应遵循统一格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "timestamp": "2025-01-04T12:00:00"
}
```

**字段说明**:
- `success`: 布尔值，表示请求是否成功
- `message`: 字符串，描述操作结果
- `data`: 对象/数组，实际返回的数据（可选）
- `timestamp`: ISO 8601格式的时间戳

---

## 错误码

| 错误码 | HTTP状态码 | 描述 |
|--------|-----------|------|
| `DOCUMENT_NOT_FOUND` | 404 | 文档不存在 |
| `INVALID_FILE` | 400 | 无效的文件 |
| `FILE_TOO_LARGE` | 400 | 文件过大 |
| `UNSUPPORTED_FILE_TYPE` | 400 | 不支持的文件类型 |
| `POE_API_ERROR` | 502 | Poe API调用失败 |
| `PROCESSING_ERROR` | 500 | 文档处理失败 |
| `VALIDATION_ERROR` | 400 | 参数验证失败 |
| `INTERNAL_ERROR` | 500 | 内部服务器错误 |

**错误响应示例**:
```json
{
  "success": false,
  "message": "文档不存在",
  "errorCode": "DOCUMENT_NOT_FOUND",
  "timestamp": "2025-01-04T12:00:00"
}
```

---

## 健康检查API

### 1. 系统健康检查

**端点**: `GET /health`

**描述**: 检查系统整体健康状态

**请求示例**:
```bash
curl http://localhost:30080/api/health
```

**响应示例**:
```json
{
  "success": true,
  "message": "系统运行正常",
  "data": {
    "status": "UP",
    "database": "UP",
    "redis": "UP",
    "poeApi": "UP",
    "timestamp": "2025-01-04T12:00:00"
  },
  "timestamp": "2025-01-04T12:00:00"
}
```

---

## 文档管理API

### 1. 上传文档

**端点**: `POST /documents/upload`

**描述**: 上传PDF文档并自动处理

**Content-Type**: `multipart/form-data`

**请求参数**:
- `file`: PDF文件（必需，最大10MB）

**请求示例**:
```bash
curl -X POST http://localhost:30080/api/documents/upload \
  -F "file=@sample.pdf"
```

**响应示例**:
```json
{
  "success": true,
  "message": "文档上传成功",
  "data": {
    "id": 1,
    "filename": "sample.pdf",
    "fileSize": 1048576,
    "status": "PROCESSING",
    "uploadedAt": "2025-01-04T12:00:00",
    "processedAt": null,
    "chunkCount": 0
  },
  "timestamp": "2025-01-04T12:00:00"
}
```

### 2. 获取文档列表

**端点**: `GET /documents`

**描述**: 获取所有已上传的文档列表

**请求示例**:
```bash
curl http://localhost:30080/api/documents
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取文档列表成功",
  "data": [
    {
      "id": 1,
      "filename": "sample.pdf",
      "fileSize": 1048576,
      "status": "COMPLETED",
      "uploadedAt": "2025-01-04T12:00:00",
      "processedAt": "2025-01-04T12:01:00",
      "chunkCount": 15
    },
    {
      "id": 2,
      "filename": "guide.pdf",
      "fileSize": 2097152,
      "status": "PROCESSING",
      "uploadedAt": "2025-01-04T12:05:00",
      "processedAt": null,
      "chunkCount": 0
    }
  ],
  "timestamp": "2025-01-04T12:10:00"
}
```

### 3. 获取文档详情

**端点**: `GET /documents/{id}`

**描述**: 获取指定文档的详细信息

**路径参数**:
- `id`: 文档ID（必需）

**请求示例**:
```bash
curl http://localhost:30080/api/documents/1
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取文档详情成功",
  "data": {
    "id": 1,
    "filename": "sample.pdf",
    "fileSize": 1048576,
    "status": "COMPLETED",
    "uploadedAt": "2025-01-04T12:00:00",
    "processedAt": "2025-01-04T12:01:00",
    "chunkCount": 15
  },
  "timestamp": "2025-01-04T12:10:00"
}
```

### 4. 删除文档

**端点**: `DELETE /documents/{id}`

**描述**: 删除指定文档及其所有片段

**路径参数**:
- `id`: 文档ID（必需）

**请求示例**:
```bash
curl -X DELETE http://localhost:30080/api/documents/1
```

**响应示例**:
```json
{
  "success": true,
  "message": "文档删除成功",
  "timestamp": "2025-01-04T12:15:00"
}
```

### 5. 获取文档统计

**端点**: `GET /documents/stats`

**描述**: 获取文档统计信息

**请求示例**:
```bash
curl http://localhost:30080/api/documents/stats
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取统计信息成功",
  "data": {
    "totalDocuments": 5,
    "completedDocuments": 4,
    "processingDocuments": 1,
    "failedDocuments": 0,
    "totalChunks": 75,
    "totalSize": 10485760
  },
  "timestamp": "2025-01-04T12:20:00"
}
```

---

## 查询API

### 1. 提交查询

**端点**: `POST /query`

**描述**: 提交查询请求，系统自动选择NLP或RAG模式

**请求体**:
```json
{
  "question": "什么是机器学习？",
  "mode": "AUTO"
}
```

**字段说明**:
- `question`: 查询问题（必需，1-500字符）
- `mode`: 查询模式（可选，默认AUTO）
  - `AUTO`: 自动选择
  - `NLP`: 纯NLP模式
  - `RAG`: RAG模式

**请求示例**:
```bash
curl -X POST http://localhost:30080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "question": "什么是机器学习？",
    "mode": "AUTO"
  }'
```

**响应示例（RAG模式）**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "answer": "机器学习是人工智能的一个分支，它使计算机系统能够从数据中学习并改进性能，而无需明确编程。主要包括监督学习、无监督学习和强化学习三大类。",
    "mode": "RAG",
    "sources": [
      {
        "documentId": 1,
        "documentName": "AI基础.pdf",
        "chunkId": 5,
        "content": "机器学习（Machine Learning）是人工智能的核心技术之一...",
        "similarity": 0.92,
        "pageNumber": 3
      },
      {
        "documentId": 1,
        "documentName": "AI基础.pdf",
        "chunkId": 8,
        "content": "监督学习是机器学习的主要方法...",
        "similarity": 0.85,
        "pageNumber": 5
      }
    ],
    "processingTime": 1250
  },
  "timestamp": "2025-01-04T12:25:00"
}
```

**响应示例（NLP模式）**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "answer": "你好！我是AI助手，很高兴为您服务。",
    "mode": "NLP",
    "sources": [],
    "processingTime": 800
  },
  "timestamp": "2025-01-04T12:26:00"
}
```

---

## 历史记录API

### 1. 获取查询历史

**端点**: `GET /history`

**描述**: 获取最近的查询历史记录

**查询参数**:
- `limit`: 返回记录数（可选，默认20，最大100）

**请求示例**:
```bash
curl "http://localhost:30080/api/history?limit=10"
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取历史记录成功",
  "data": [
    {
      "id": 15,
      "question": "什么是机器学习？",
      "answer": "机器学习是人工智能的一个分支...",
      "mode": "RAG",
      "sourceCount": 2,
      "processingTime": 1250,
      "createdAt": "2025-01-04T12:25:00"
    },
    {
      "id": 14,
      "question": "你好",
      "answer": "你好！我是AI助手...",
      "mode": "NLP",
      "sourceCount": 0,
      "processingTime": 800,
      "createdAt": "2025-01-04T12:20:00"
    }
  ],
  "timestamp": "2025-01-04T12:30:00"
}
```

### 2. 获取历史详情

**端点**: `GET /history/{id}`

**描述**: 获取指定历史记录的详细信息（包含来源引用）

**路径参数**:
- `id`: 历史记录ID（必需）

**请求示例**:
```bash
curl http://localhost:30080/api/history/15
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取历史详情成功",
  "data": {
    "id": 15,
    "question": "什么是机器学习？",
    "answer": "机器学习是人工智能的一个分支...",
    "mode": "RAG",
    "sources": [
      {
        "documentId": 1,
        "documentName": "AI基础.pdf",
        "chunkId": 5,
        "content": "机器学习（Machine Learning）是人工智能的核心技术之一...",
        "similarity": 0.92,
        "pageNumber": 3
      }
    ],
    "processingTime": 1250,
    "createdAt": "2025-01-04T12:25:00"
  },
  "timestamp": "2025-01-04T12:35:00"
}
```

### 3. 清空历史记录

**端点**: `DELETE /history`

**描述**: 清空所有查询历史记录

**请求示例**:
```bash
curl -X DELETE http://localhost:30080/api/history
```

**响应示例**:
```json
{
  "success": true,
  "message": "历史记录已清空",
  "timestamp": "2025-01-04T12:40:00"
}
```

---

## 使用示例

### 完整工作流程

```bash
# 1. 检查系统健康
curl http://localhost:30080/api/health

# 2. 上传文档
curl -X POST http://localhost:30080/api/documents/upload \
  -F "file=@machine-learning.pdf"

# 3. 等待处理完成，检查文档状态
curl http://localhost:30080/api/documents/1

# 4. 提交查询
curl -X POST http://localhost:30080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "question": "什么是监督学习？",
    "mode": "AUTO"
  }'

# 5. 查看历史记录
curl http://localhost:30080/api/history?limit=5

# 6. 获取文档统计
curl http://localhost:30080/api/documents/stats
```

---

## 注意事项

1. **文件大小限制**: 单个PDF文件最大10MB
2. **支持的文件类型**: 仅支持PDF格式
3. **查询长度限制**: 问题长度1-500字符
4. **处理时间**: 文档处理为异步操作，大文件可能需要几分钟
5. **缓存策略**: 查询结果缓存1小时，相同问题会直接返回缓存结果
6. **并发限制**: 建议控制并发上传数量，避免系统过载

---

## 更新日志

### v1.0.0 (2025-01-04)
- 初始版本发布
- 支持文档上传、查询、历史记录管理
- 实现智能路由（NLP/RAG自动选择）
- 集成Poe API
- 支持全文搜索和语义检索
