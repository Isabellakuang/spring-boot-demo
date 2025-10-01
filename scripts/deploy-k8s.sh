#!/bin/bash

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  部署到 Kubernetes${NC}"
echo -e "${GREEN}========================================${NC}"

# 检查 kubectl
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}错误: kubectl 未安装${NC}"
    exit 1
fi

# 检查 kustomize
if ! command -v kustomize &> /dev/null; then
    echo -e "${YELLOW}警告: kustomize 未安装，使用 kubectl apply -k${NC}"
    USE_KUBECTL_KUSTOMIZE=true
fi

echo -e "${GREEN}[1/4] 创建命名空间...${NC}"
kubectl apply -f infra/k8s/namespace.yaml

echo -e "${GREEN}[2/4] 应用配置和密钥...${NC}"
echo -e "${YELLOW}警告: 请先编辑 infra/k8s/secrets.yaml 并设置生产环境密钥！${NC}"
read -p "是否继续? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

kubectl apply -f infra/k8s/configmap.yaml
kubectl apply -f infra/k8s/secrets.yaml

echo -e "${GREEN}[3/4] 部署应用...${NC}"
if [ "$USE_KUBECTL_KUSTOMIZE" = true ]; then
    kubectl apply -k infra/k8s/
else
    kustomize build infra/k8s/ | kubectl apply -f -
fi

echo -e "${GREEN}[4/4] 等待部署完成...${NC}"
kubectl rollout status deployment/backend -n spring-boot-demo
kubectl rollout status deployment/frontend -n spring-boot-demo
kubectl rollout status deployment/ml-service -n spring-boot-demo

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  部署完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "查看 Pods: ${YELLOW}kubectl get pods -n spring-boot-demo${NC}"
echo -e "查看服务: ${YELLOW}kubectl get svc -n spring-boot-demo${NC}"
echo -e "查看日志: ${YELLOW}kubectl logs -f deployment/backend -n spring-boot-demo${NC}"