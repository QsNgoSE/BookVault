#!/bin/bash

# BookVault Local Development Startup Script
# This script starts all services for local development

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

echo "🚀 Starting BookVault Local Development Environment"
echo "=================================================="

# Check if PostgreSQL is running
if ! pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
    print_error "PostgreSQL is not running. Please start PostgreSQL first."
    print_info "macOS: brew services start postgresql@14"
    print_info "Ubuntu: sudo systemctl start postgresql"
    exit 1
fi

# Check if Redis is running
if ! redis-cli ping >/dev/null 2>&1; then
    print_error "Redis is not running. Please start Redis first."
    print_info "macOS: brew services start redis"
    print_info "Ubuntu: sudo systemctl start redis-server"
    exit 1
fi

print_status "PostgreSQL and Redis are running"

# Create database if it doesn't exist
psql -h localhost -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'bookvault'" | grep -q 1 || {
    print_info "Creating database 'bookvault'..."
    psql -h localhost -U postgres -c "CREATE DATABASE bookvault;"
    psql -h localhost -U postgres -c "CREATE USER bookvault WITH PASSWORD 'bookvault123';"
    psql -h localhost -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE bookvault TO bookvault;"
}

print_status "Database setup complete"

# Function to start a service in a new terminal
start_service() {
    local service_name=$1
    local service_path=$2
    local profile=$3
    
    print_info "Starting $service_name..."
    
    # For macOS, use Terminal.app
    if [[ "$OSTYPE" == "darwin"* ]]; then
        osascript -e "tell application \"Terminal\" to do script \"cd '$PWD/$service_path' && mvn spring-boot:run -Dspring-boot.run.profiles=$profile\""
    # For Linux, try to use gnome-terminal or xterm
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        if command -v gnome-terminal &> /dev/null; then
            gnome-terminal -- bash -c "cd '$PWD/$service_path' && mvn spring-boot:run -Dspring-boot.run.profiles=$profile; exec bash"
        elif command -v xterm &> /dev/null; then
            xterm -hold -e "cd '$PWD/$service_path' && mvn spring-boot:run -Dspring-boot.run.profiles=$profile" &
        else
            print_warning "No terminal emulator found. Please start $service_name manually:"
            print_info "cd $service_path && mvn spring-boot:run -Dspring-boot.run.profiles=$profile"
        fi
    else
        print_warning "Unsupported OS. Please start $service_name manually:"
        print_info "cd $service_path && mvn spring-boot:run -Dspring-boot.run.profiles=$profile"
    fi
}

# Start services
start_service "Auth Service" "backend/auth-service" "local"
sleep 2
start_service "Book Service" "backend/book-service" "local"

print_status "Services are starting..."
print_info "Auth Service will be available at: http://localhost:8082"
print_info "Book Service will be available at: http://localhost:8083"
print_info "Frontend will be available at: http://localhost:8080"

# Wait a bit for services to start
sleep 3

# Start frontend
print_info "Starting frontend..."
if command -v python3 &> /dev/null; then
    python3 -m http.server 8080 &
elif command -v python &> /dev/null; then
    python -m http.server 8080 &
elif command -v npx &> /dev/null; then
    npx serve . -p 8080 &
else
    print_warning "No Python or Node.js found. Please start the frontend manually:"
    print_info "python3 -m http.server 8080"
fi

print_status "Local development environment is starting!"
print_info "Visit http://localhost:8080 to access BookVault"
print_info "Press Ctrl+C to stop the frontend server"

# Keep the script running
wait 