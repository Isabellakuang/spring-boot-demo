.PHONY: help build deploy clean logs test dev stop

help:
	@echo "SCB RAG Demo - 可用命令:"
	@echo "  make dev     - 启动本地开发环境 (Docker Compose)"
	@echo "  make build   - 构建所有 Docker 镜像"
	@echo "  make deploy  - 部署到 Kubernetes"
	@echo "  make clean   - 清理 Kubernetes 资源"
	@echo "  make logs    - 查看服务日志"
	@echo "  make test    - 运行测试"
	@echo "  make stop    - 停止本地开发环境"

dev:
	@echo "启动本地开发环境..."
	docker-compose up -d
	@echo "服务已启动:"
	@echo "  前端: http://localhost:3000"
	@echo "  后端: http://localhost:8080"
	@echo "  API文档: http://localhost:8080/swagger-ui.html"

stop:
	@echo "停止本地开发环境..."
	docker-compose down

build:
	@echo "构建 Docker 镜像..."
	python scripts/build.py

deploy:
	@echo "部署到 Kubernetes..."
	python scripts/deploy.py

clean:
	@echo "清理 Kubernetes 资源..."
	python scripts/clean.py

logs:
	@echo "查看服务日志..."
	python scripts/logs.py

test:
	@echo "运行测试..."
	cd backend && ./mvnw test
	cd frontend && npm test
