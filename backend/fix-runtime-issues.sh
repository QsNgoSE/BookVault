#!/bin/bash

echo "🔧 BookVault Runtime Issue Fixes"
echo "================================="
echo ""

# Set JAVA_HOME for proper Java version
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
echo "☕ JAVA_HOME set to: $JAVA_HOME"

# Check if Docker services are running
echo ""
echo "🐳 Checking Docker Services..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check PostgreSQL
if docker-compose ps postgres | grep -q "Up"; then
    echo "✅ PostgreSQL is running"
else
    echo "❌ PostgreSQL is not running. Starting..."
    docker-compose up -d postgres
    echo "⏳ Waiting for PostgreSQL to be ready..."
    sleep 10
fi

# Check Redis  
if docker-compose ps redis | grep -q "Up"; then
    echo "✅ Redis is running"
else
    echo "❌ Redis is not running. Starting..."
    docker-compose up -d redis
    echo "⏳ Waiting for Redis to be ready..."
    sleep 5
fi

echo ""
echo "🔌 Testing Database Connectivity..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Test PostgreSQL connection
if docker-compose exec -T postgres psql -U bookvault -d bookvault_auth -c "SELECT 1;" >/dev/null 2>&1; then
    echo "✅ PostgreSQL connection successful"
else
    echo "❌ PostgreSQL connection failed"
    echo "   Trying to create database and user..."
    docker-compose exec -T postgres psql -U postgres -c "CREATE DATABASE bookvault_auth;" 2>/dev/null || true
    docker-compose exec -T postgres psql -U postgres -c "CREATE USER bookvault WITH PASSWORD 'bookvault123';" 2>/dev/null || true
    docker-compose exec -T postgres psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE bookvault_auth TO bookvault;" 2>/dev/null || true
fi

# Test Redis connection
if docker-compose exec -T redis redis-cli ping >/dev/null 2>&1; then
    echo "✅ Redis connection successful"
else
    echo "❌ Redis connection failed"
fi

echo ""
echo "🔨 Building Auth Service..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
cd auth-service
mvn clean package -DskipTests -q

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
else
    echo "❌ Build failed"
    exit 1
fi

echo ""
echo "🚀 Starting Auth Service with Local Profile..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   Profile: local"
echo "   Port: 8082"
echo "   Database: PostgreSQL (Docker)"
echo "   Cache: Redis (Docker)"
echo ""
echo "📍 Service URLs:"
echo "   🔐 Auth Service: http://localhost:8082/api/auth/health"
echo "   📋 Health Check: http://localhost:8082/actuator/health"
echo ""
echo "🛑 Press Ctrl+C to stop the service"
echo ""

# Run with local profile
java -jar -Dspring.profiles.active=local target/auth-service-1.0.0.jar 