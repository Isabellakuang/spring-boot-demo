#!/usr/bin/env python3
"""
SCB RAG Demo - Deployment Script (Python)
For Docker Desktop Kubernetes environment
"""

import subprocess
import sys
import time

# ANSI color codes
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    WHITE = '\033[97m'
    RESET = '\033[0m'

def print_color(message, color):
    """Print colored message"""
    print(f"{color}{message}{Colors.RESET}")

def run_command(command, check=True):
    """Run shell command"""
    try:
        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            check=check
        )
        return result.returncode == 0
    except subprocess.CalledProcessError:
        return False

def wait_for_pod(label, namespace="scb-rag-demo", timeout_seconds=300):
    """Wait for pod to be ready"""
    print_color(f"Waiting for pod with label {label} to be ready...", Colors.YELLOW)
    elapsed = 0
    
    while elapsed < timeout_seconds:
        result = subprocess.run(
            f'kubectl get pods -n {namespace} -l {label} -o jsonpath="{{.items[0].status.conditions[?(@.type==\\"Ready\\")].status}}"',
            shell=True,
            capture_output=True,
            text=True
        )
        
        if result.stdout.strip() == "True":
            print_color("✓ Pod is ready", Colors.GREEN)
            return True
        
        time.sleep(5)
        elapsed += 5
        print(".", end="", flush=True)
    
    print()
    print_color("✗ Timeout waiting for pod to be ready", Colors.RED)
    return False

def deploy_postgres():
    """Deploy PostgreSQL"""
    print_color("=" * 40, Colors.CYAN)
    print_color("Deploying PostgreSQL", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    run_command("kubectl apply -f k8s/postgres/postgres-pvc.yaml")
    run_command("kubectl apply -f k8s/postgres/postgres-deployment.yaml")
    run_command("kubectl apply -f k8s/postgres/postgres-service.yaml")
    
    if not wait_for_pod("app=postgres"):
        raise Exception("PostgreSQL deployment failed")
    print()

def deploy_redis():
    """Deploy Redis"""
    print_color("=" * 40, Colors.CYAN)
    print_color("Deploying Redis", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    run_command("kubectl apply -f k8s/redis/redis-deployment.yaml")
    run_command("kubectl apply -f k8s/redis/redis-service.yaml")
    
    if not wait_for_pod("app=redis"):
        raise Exception("Redis deployment failed")
    print()

def deploy_backend():
    """Deploy Backend"""
    print_color("=" * 40, Colors.CYAN)
    print_color("Deploying Backend", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    run_command("kubectl apply -f k8s/backend/backend-uploads-pvc.yaml")
    run_command("kubectl apply -f k8s/backend/backend-deployment.yaml")
    run_command("kubectl apply -f k8s/backend/backend-service.yaml")
    
    if not wait_for_pod("app=backend"):
        raise Exception("Backend deployment failed")
    print()

def deploy_frontend():
    """Deploy Frontend"""
    print_color("=" * 40, Colors.CYAN)
    print_color("Deploying Frontend", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    run_command("kubectl apply -f k8s/frontend/frontend-deployment.yaml")
    run_command("kubectl apply -f k8s/frontend/frontend-service.yaml")
    
    if not wait_for_pod("app=frontend"):
        raise Exception("Frontend deployment failed")
    print()

def deploy_hpa():
    """Deploy Horizontal Pod Autoscaler"""
    print_color("=" * 40, Colors.CYAN)
    print_color("Deploying HPA (Horizontal Pod Autoscaler)", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    run_command("kubectl apply -f k8s/hpa.yaml")
    print_color("✓ HPA configured", Colors.GREEN)
    print()

def show_status():
    """Show deployment status"""
    print_color("=" * 40, Colors.CYAN)
    print_color("Deployment Status", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    print_color("\nPods:", Colors.YELLOW)
    subprocess.run("kubectl get pods -n scb-rag-demo", shell=True)
    
    print_color("\nServices:", Colors.YELLOW)
    subprocess.run("kubectl get services -n scb-rag-demo", shell=True)
    
    print_color("\nPersistentVolumeClaims:", Colors.YELLOW)
    subprocess.run("kubectl get pvc -n scb-rag-demo", shell=True)
    
    print_color("\nHorizontal Pod Autoscalers:", Colors.YELLOW)
    subprocess.run("kubectl get hpa -n scb-rag-demo", shell=True)
    print()

def show_access_info():
    """Show access information"""
    print_color("=" * 40, Colors.GREEN)
    print_color("Deployment Complete!", Colors.GREEN)
    print_color("=" * 40, Colors.GREEN)
    print()
    print_color("Access the application at:", Colors.YELLOW)
    print_color("  Frontend: http://localhost:30080", Colors.WHITE)
    print_color("  Backend API: http://localhost:30081", Colors.WHITE)
    print_color("  Swagger UI: http://localhost:30081/swagger-ui.html", Colors.WHITE)
    print()
    print_color("Useful commands:", Colors.YELLOW)
    print_color("  View logs: python scripts/logs.py", Colors.WHITE)
    print_color("  Clean up: python scripts/clean.py", Colors.WHITE)
    print()

def main():
    """Main deployment flow"""
    print_color("=" * 40, Colors.CYAN)
    print_color("SCB RAG Demo - Kubernetes Deployment", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    print()
    
    # Check Kubernetes
    print_color("Checking Kubernetes environment...", Colors.YELLOW)
    if not run_command("kubectl cluster-info", check=False):
        print_color("✗ Error: Cannot connect to Kubernetes", Colors.RED)
        print_color("Please ensure Kubernetes is enabled in Docker Desktop", Colors.YELLOW)
        sys.exit(1)
    print_color("✓ Kubernetes is connected", Colors.GREEN)
    print()
    
    try:
        # Create namespace
        print_color("Creating namespace...", Colors.YELLOW)
        run_command("kubectl apply -f k8s/namespace.yaml")
        print_color("✓ Namespace created", Colors.GREEN)
        print()
        
        # Create ConfigMap and Secret
        print_color("Creating ConfigMap and Secret...", Colors.YELLOW)
        run_command("kubectl apply -f k8s/configmap.yaml")
        run_command("kubectl apply -f k8s/secret.yaml")
        print_color("✓ ConfigMap and Secret created", Colors.GREEN)
        print()
        
        # Deploy components in order
        deploy_postgres()
        deploy_redis()
        
        # Wait for databases to be fully ready
        print_color("Waiting for databases to be fully ready...", Colors.YELLOW)
        time.sleep(10)
        print_color("✓ Databases ready", Colors.GREEN)
        print()
        
        deploy_backend()
        deploy_frontend()
        
        # Deploy HPA
        deploy_hpa()
        
        # Show status
        show_status()
        show_access_info()
        
    except Exception as e:
        print()
        print_color("=" * 40, Colors.RED)
        print_color("Deployment Failed!", Colors.RED)
        print_color("=" * 40, Colors.RED)
        print_color(f"Error: {str(e)}", Colors.RED)
        print()
        print_color("Check the logs with: python scripts/logs.py", Colors.YELLOW)
        sys.exit(1)

if __name__ == "__main__":
    main()
