#!/bin/bash

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  启动 Spring Boot Demo 开发环境${NC}"
echo -e "${GREEN}========================================${NC}"

# 检查 .env 文件
if [ ! -f .env ]; then
    echo -e "${YELLOW}警告: .env 文件不存在，从 .env.example 复制...${NC}"
    cp .env.example .env
    echo -e "${YELLOW}请编辑 .env 文件并填入您的 API keys${NC}"
    exit 1
fi

# 检查必需的环境变量
source .env
if [ -z "$OPENAI_API_KEY" ] || [ "$OPENAI_API_KEY" = "sk-your-openai-api-key-here" ]; then
    echo -e "${RED}错误: 请在 .env 文件中设置有效的 OPENAI_API_KEY${NC}"
    exit 1
fi

echo -e "${GREEN}[1/5] 停止现有容器...${NC}"
cd infra
docker-compose down

echo -e "${GREEN}[2/5] 构建镜像...${NC}"
docker-compose build

echo -e "${GREEN}[3/5] 启动服务...${NC}"
docker-compose up -d

echo -e "${GREEN}[4/5] 等待服务启动...${NC}"
sleep 30

echo -e "${GREEN}[5/5] 检查服务健康状态...${NC}"
docker-compose ps

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  服务启动完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "访问地址："
echo -e "  前端:           ${YELLOW}http://localhost:3000${NC}"
echo -e "  后端 API:       ${YELLOW}http://localhost:8080${NC}"
echo -e "  Swagger UI:     ${YELLOW}http://localhost:8083${NC}"
echo -e "  Airflow:        ${YELLOW}http://localhost:8082${NC} (admin/admin)"
echo -e "  Grafana:        ${YELLOW}http://localhost:3001${NC} (admin/admin)"
echo -e "  Prometheus:     ${YELLOW}http://localhost:9090${NC}"
echo ""
echo -e "查看日志: ${YELLOW}docker-compose logs -f [service-name]${NC}"
echo -e "停止服务: ${YELLOW}./scripts/stop-dev.sh${NC}"