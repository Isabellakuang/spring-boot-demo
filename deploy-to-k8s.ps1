# Spring Boot Demo K8S Deployment Script

Write-Host "=== Spring Boot Demo K8S Deployment ===" -ForegroundColor Green

# 1. Build Backend JAR
Write-Host "`n[1/7] Building backend application..." -ForegroundColor Yellow
Set-Location backend
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Backend build failed!" -ForegroundColor Red
    exit 1
}
Set-Location ..

# 2. Build Docker Images
Write-Host "`n[2/7] Building Docker images..." -ForegroundColor Yellow

# Backend
Write-Host "Building backend image..." -ForegroundColor Cyan
docker build -t backend:latest ./backend --no-cache
if ($LASTEXITCODE -ne 0) {
    Write-Host "Backend image build failed!" -ForegroundColor Red
    exit 1
}

# ML Service
Write-Host "Building ml-service image..." -ForegroundColor Cyan
docker build -t ml-service:latest ./python-services/ml-service
if ($LASTEXITCODE -ne 0) {
    Write-Host "ML Service image build failed!" -ForegroundColor Red
    exit 1
}

# Frontend
Write-Host "Building frontend image..." -ForegroundColor Cyan
docker build -t frontend:latest ./frontend
if ($LASTEXITCODE -ne 0) {
    Write-Host "Frontend image build failed!" -ForegroundColor Red
    exit 1
}

# 3. Create Namespace
Write-Host "`n[3/7] Creating K8S namespace..." -ForegroundColor Yellow
kubectl apply -f infra/k8s/namespace.yaml

# 4. Deploy ConfigMap
Write-Host "`n[4/7] Deploying ConfigMap..." -ForegroundColor Yellow
kubectl apply -f infra/k8s/configmap.yaml

# 5. Deploy PostgreSQL
Write-Host "`n[5/7] Deploying PostgreSQL..." -ForegroundColor Yellow
kubectl apply -f infra/k8s/postgres-deployment.yaml

# Wait for PostgreSQL to be ready
Write-Host "Waiting for PostgreSQL to be ready..." -ForegroundColor Cyan
kubectl wait --for=condition=ready pod -l app=postgres -n spring-boot-demo --timeout=120s

# 6. Deploy Application Services
Write-Host "`n[6/7] Deploying application services..." -ForegroundColor Yellow
kubectl apply -f infra/k8s/ml-service-deployment.yaml
kubectl apply -f infra/k8s/backend-deployment.yaml
kubectl apply -f infra/k8s/frontend-deployment.yaml

# 7. Wait for All Pods to be Ready
Write-Host "`n[7/7] Waiting for all services to be ready..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pod -l app=ml-service -n spring-boot-demo --timeout=120s
kubectl wait --for=condition=ready pod -l app=backend -n spring-boot-demo --timeout=180s
kubectl wait --for=condition=ready pod -l app=frontend -n spring-boot-demo --timeout=120s

# Display Deployment Status
Write-Host "`n=== Deployment Complete ===" -ForegroundColor Green
Write-Host "`nService Status:" -ForegroundColor Yellow
kubectl get pods -n spring-boot-demo
kubectl get svc -n spring-boot-demo

Write-Host "`nAccess URLs:" -ForegroundColor Yellow
Write-Host "Frontend: http://localhost:30000" -ForegroundColor Cyan
Write-Host "Backend API: http://localhost:30080" -ForegroundColor Cyan

Write-Host "`nTip: Use the following commands to view logs:" -ForegroundColor Yellow
Write-Host "kubectl logs -f deployment/frontend -n spring-boot-demo" -ForegroundColor Gray
Write-Host "kubectl logs -f deployment/backend -n spring-boot-demo" -ForegroundColor Gray
Write-Host "kubectl logs -f deployment/ml-service -n spring-boot-demo" -ForegroundColor Gray
