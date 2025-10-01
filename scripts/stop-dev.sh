#!/bin/bash

GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${GREEN}停止所有服务...${NC}"
cd infra
docker-compose down

echo -e "${GREEN}服务已停止${NC}"