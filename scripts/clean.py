#!/usr/bin/env python3
"""
SCB RAG Demo - Kubernetes Cleanup Script (Python)
Purpose: Clean up all Kubernetes resources
"""

import subprocess
import sys
import time

# ANSI color codes
class Colors:
    BLUE = '\033[94m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    RESET = '\033[0m'

def print_info(message):
    """Print info message"""
    print(f"{Colors.BLUE}[INFO] {message}{Colors.RESET}")

def print_success(message):
    """Print success message"""
    print(f"{Colors.GREEN}[SUCCESS] {message}{Colors.RESET}")

def print_warning(message):
    """Print warning message"""
    print(f"{Colors.YELLOW}[WARNING] {message}{Colors.RESET}")

def print_error(message):
    """Print error message"""
    print(f"{Colors.RED}[ERROR] {message}{Colors.RESET}")

def run_command(command, check=False):
    """Run shell command"""
    try:
        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            check=check
        )
        return result.returncode == 0, result.stdout, result.stderr
    except subprocess.CalledProcessError as e:
        return False, e.stdout, e.stderr

def check_kubectl():
    """Check if kubectl is available"""
    success, _, _ = run_command("kubectl version --client")
    if not success:
        print_error("kubectl is not installed or not in PATH")
        sys.exit(1)

def check_namespace():
    """Check if namespace exists"""
    success, _, _ = run_command("kubectl get namespace scb-rag-demo")
    if not success:
        print_error("Namespace scb-rag-demo does not exist")
        print_info("Please run 'python scripts/deploy.py' to deploy the application first")
        sys.exit(1)

def confirm_cleanup():
    """Confirm cleanup operation"""
    print()
    print_warning("=" * 40)
    print_warning("WARNING: This will delete all resources")
    print_warning("=" * 40)
    print()
    print_warning("The following resources will be deleted:")
    print_warning("  - Namespace: scb-rag-demo")
    print_warning("  - All Pods, Services, Deployments")
    print_warning("  - ConfigMaps, Secrets")
    print_warning("  - PersistentVolumeClaims (data will be lost)")
    print_warning("  - HPA (Horizontal Pod Autoscaler) configurations")
    print()
    
    confirm = input("Confirm deletion? (yes/no): ").strip().lower()
    if confirm != "yes":
        print_info("Operation cancelled")
        sys.exit(0)

def delete_namespace():
    """Delete namespace (will cascade delete all resources)"""
    print_info("Deleting namespace scb-rag-demo...")
    
    success, _, _ = run_command("kubectl get namespace scb-rag-demo")
    if success:
        run_command("kubectl delete namespace scb-rag-demo")
        
        print_info("Waiting for namespace to be fully deleted...")
        while True:
            success, _, _ = run_command("kubectl get namespace scb-rag-demo")
            if not success:
                break
            print(".", end="", flush=True)
            time.sleep(2)
        print()
        
        print_success("Namespace deleted")
    else:
        print_warning("Namespace scb-rag-demo does not exist")

def delete_docker_images():
    """Clean up Docker images (optional)"""
    print()
    clean_images = input("Also delete Docker images? (yes/no): ").strip().lower()
    
    if clean_images == "yes":
        print_info("Deleting Docker images...")
        
        success, stdout, _ = run_command("docker images")
        if "scb-rag-backend" in stdout:
            run_command("docker rmi scb-rag-backend:latest")
            print_success("Backend image deleted")
        
        if "scb-rag-frontend" in stdout:
            run_command("docker rmi scb-rag-frontend:latest")
            print_success("Frontend image deleted")

def show_result():
    """Display cleanup results"""
    print()
    print_info("=" * 40)
    print_info("Cleanup Results")
    print_info("=" * 40)
    
    print()
    print_info("Checking namespace:")
    success, stdout, _ = run_command("kubectl get namespaces")
    if "scb-rag-demo" not in stdout:
        print_success("✓ Namespace completely deleted")
    
    print()
    print_info("Checking Docker images:")
    success, stdout, _ = run_command("docker images")
    if "scb-rag" not in stdout:
        print_success("✓ Docker images deleted")

def main():
    """Main function"""
    print()
    print_info("=" * 40)
    print_info("SCB RAG Demo - Kubernetes Cleanup")
    print_info("=" * 40)
    
    # Check kubectl
    check_kubectl()
    
    # Confirm operation
    confirm_cleanup()
    
    print()
    print_info("Starting cleanup...")
    print()
    
    # Delete resources
    delete_namespace()
    
    # Optional: Clean up Docker images
    delete_docker_images()
    
    # Display results
    show_result()
    
    print()
    print_success("=" * 40)
    print_success("✓ Cleanup completed!")
    print_success("=" * 40)
    print()

if __name__ == "__main__":
    main()
