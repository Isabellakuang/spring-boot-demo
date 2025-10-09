#!/usr/bin/env python3
"""
SCB RAG Demo - Log Viewer Script (Python)
Purpose: View Kubernetes Pod logs
"""

import subprocess
import sys

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
        return result.returncode == 0
    except subprocess.CalledProcessError:
        return False

def check_kubectl():
    """Check if kubectl is available"""
    if not run_command("kubectl version --client"):
        print_error("kubectl is not installed or not in PATH")
        sys.exit(1)

def check_namespace():
    """Check if namespace exists"""
    if not run_command("kubectl get namespace scb-rag-demo"):
        print_error("Namespace scb-rag-demo does not exist")
        print_info("Please run 'python scripts/deploy.py' to deploy the application first")
        sys.exit(1)

def show_menu():
    """Display menu"""
    print()
    print_info("=" * 40)
    print_info("SCB RAG Demo - Log Viewer")
    print_info("=" * 40)
    print()
    print("Select logs to view:")
    print("  1) Backend Application")
    print("  2) Frontend Application")
    print("  3) PostgreSQL")
    print("  4) Redis")
    print("  5) All Pods Status")
    print("  6) View All Pods Logs (Last 100 lines)")
    print("  0) Exit")
    print()

def show_backend_logs():
    """View backend logs"""
    print_info("Viewing backend application logs (Press Ctrl+C to exit)...")
    try:
        subprocess.run("kubectl logs -f -l app=backend -n scb-rag-demo --tail=100", shell=True)
    except KeyboardInterrupt:
        print("\n")

def show_frontend_logs():
    """View frontend logs"""
    print_info("Viewing frontend application logs (Press Ctrl+C to exit)...")
    try:
        subprocess.run("kubectl logs -f -l app=frontend -n scb-rag-demo --tail=100", shell=True)
    except KeyboardInterrupt:
        print("\n")

def show_postgres_logs():
    """View PostgreSQL logs"""
    print_info("Viewing PostgreSQL logs (Press Ctrl+C to exit)...")
    try:
        subprocess.run("kubectl logs -f -l app=postgres -n scb-rag-demo --tail=100", shell=True)
    except KeyboardInterrupt:
        print("\n")

def show_redis_logs():
    """View Redis logs"""
    print_info("Viewing Redis logs (Press Ctrl+C to exit)...")
    try:
        subprocess.run("kubectl logs -f -l app=redis -n scb-rag-demo --tail=100", shell=True)
    except KeyboardInterrupt:
        print("\n")

def show_pods_status():
    """View all Pods status"""
    print_info("All Pods Status:")
    print()
    subprocess.run("kubectl get pods -n scb-rag-demo -o wide", shell=True)
    print()
    
    print_info("Services Status:")
    print()
    subprocess.run("kubectl get svc -n scb-rag-demo", shell=True)
    print()
    
    print_info("PersistentVolumeClaims Status:")
    print()
    subprocess.run("kubectl get pvc -n scb-rag-demo", shell=True)
    print()
    
    print_info("HPA Status:")
    print()
    subprocess.run("kubectl get hpa -n scb-rag-demo", shell=True)
    print()

def show_all_logs():
    """View all logs"""
    print_info("=" * 40)
    print_info("Backend Application Logs (Last 100 lines)")
    print_info("=" * 40)
    subprocess.run("kubectl logs -l app=backend -n scb-rag-demo --tail=100", shell=True)
    
    print()
    print_info("=" * 40)
    print_info("Frontend Application Logs (Last 100 lines)")
    print_info("=" * 40)
    subprocess.run("kubectl logs -l app=frontend -n scb-rag-demo --tail=100", shell=True)
    
    print()
    print_info("=" * 40)
    print_info("PostgreSQL Logs (Last 100 lines)")
    print_info("=" * 40)
    subprocess.run("kubectl logs -l app=postgres -n scb-rag-demo --tail=100", shell=True)
    
    print()
    print_info("=" * 40)
    print_info("Redis Logs (Last 100 lines)")
    print_info("=" * 40)
    subprocess.run("kubectl logs -l app=redis -n scb-rag-demo --tail=100", shell=True)

def main():
    """Main function"""
    # Check environment
    check_kubectl()
    check_namespace()
    
    while True:
        show_menu()
        choice = input("Enter option (0-6): ").strip()
        
        if choice == "1":
            show_backend_logs()
        elif choice == "2":
            show_frontend_logs()
        elif choice == "3":
            show_postgres_logs()
        elif choice == "4":
            show_redis_logs()
        elif choice == "5":
            show_pods_status()
            input("Press Enter to continue...")
        elif choice == "6":
            show_all_logs()
            input("Press Enter to continue...")
        elif choice == "0":
            print_info("Exiting")
            sys.exit(0)
        else:
            print_error("Invalid option, please try again")
            import time
            time.sleep(1)

if __name__ == "__main__":
    main()
