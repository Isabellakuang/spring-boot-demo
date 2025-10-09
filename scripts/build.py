#!/usr/bin/env python3
"""
SCB RAG Demo - Build Script (Python)
For Docker Desktop Kubernetes environment
"""

import subprocess
import sys
import os

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

def run_command(command, cwd=None, check=True):
    """Run shell command and return result"""
    try:
        result = subprocess.run(
            command,
            shell=True,
            cwd=cwd,
            capture_output=True,
            text=True,
            check=check
        )
        return result.returncode == 0
    except subprocess.CalledProcessError:
        return False

def check_docker():
    """Check if Docker is running"""
    print_color("Checking Docker environment...", Colors.YELLOW)
    if run_command("docker info", check=False):
        print_color("✓ Docker is running", Colors.GREEN)
        return True
    else:
        print_color("✗ Error: Docker is not running. Please start Docker Desktop", Colors.RED)
        return False

def check_kubernetes():
    """Check if Kubernetes is available"""
    print_color("Checking Kubernetes environment...", Colors.YELLOW)
    if run_command("kubectl cluster-info", check=False):
        print_color("✓ Kubernetes is connected", Colors.GREEN)
        return True
    else:
        print_color("⚠ Warning: Kubernetes is not enabled or cannot connect", Colors.YELLOW)
        print_color("Please enable Kubernetes in Docker Desktop", Colors.YELLOW)
        return False

def build_backend():
    """Build backend Docker image"""
    print()
    print_color("=" * 40, Colors.CYAN)
    print_color("1. Building Backend Image", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    print_color("Building backend Docker image...", Colors.YELLOW)
    if run_command("docker build -t scb-rag-backend:latest .", cwd="backend"):
        print_color("✓ Backend image built successfully", Colors.GREEN)
        return True
    else:
        print_color("✗ Backend image build failed", Colors.RED)
        return False

def build_frontend():
    """Build frontend Docker image"""
    print()
    print_color("=" * 40, Colors.CYAN)
    print_color("2. Building Frontend Image", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    
    print_color("Building frontend Docker image...", Colors.YELLOW)
    if run_command("docker build -t scb-rag-frontend:latest .", cwd="frontend"):
        print_color("✓ Frontend image built successfully", Colors.GREEN)
        return True
    else:
        print_color("✗ Frontend image build failed", Colors.RED)
        return False

def show_images():
    """Show built images"""
    print()
    print_color("=" * 40, Colors.CYAN)
    print_color("Built Images", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    subprocess.run("docker images | findstr scb-rag", shell=True)
    print()

def main():
    """Main function"""
    print_color("=" * 40, Colors.CYAN)
    print_color("SCB RAG Demo - Building Docker Images", Colors.CYAN)
    print_color("=" * 40, Colors.CYAN)
    print()
    
    # Check environment
    if not check_docker():
        sys.exit(1)
    
    if not check_kubernetes():
        sys.exit(1)
    
    # Build images
    if not build_backend():
        sys.exit(1)
    
    if not build_frontend():
        sys.exit(1)
    
    # Show results
    show_images()
    
    print_color("=" * 40, Colors.GREEN)
    print_color("✓ All images built successfully!", Colors.GREEN)
    print_color("=" * 40, Colors.GREEN)
    print()
    print_color("Next steps:", Colors.YELLOW)
    print_color("1. Run 'python scripts/deploy.py' to deploy to Kubernetes", Colors.WHITE)
    print_color("2. Or run 'docker-compose up -d' to start with Docker Compose", Colors.WHITE)

if __name__ == "__main__":
    main()
