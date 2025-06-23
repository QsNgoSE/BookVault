#!/bin/bash

echo "🚀 Starting BookVault Backend Services..."

# Set correct JAVA_HOME to use Java 17
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
echo "🔧 JAVA_HOME set to: $JAVA_HOME"

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    echo "Install with: brew install openjdk@17"
    exit 1
fi

# Check Java version
echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Navigate to backend directory
cd "$(dirname "$0")/backend"

echo "📦 Building services..."

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "Using Maven..."
    mvn clean package -DskipTests
elif [ -f "./mvnw" ]; then
    echo "Using Maven Wrapper..."
    ./mvnw clean package -DskipTests
else
    echo "❌ Maven not found. Please install Maven first:"
    echo "brew install maven"
    exit 1
fi

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi

echo "🎯 Starting services..."

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    local jar_path="${service_name}/target/${service_name}-1.0.0.jar"
    
    if [ -f "$jar_path" ]; then
        echo "Starting $service_name on port $port..."
        java -jar "$jar_path" &
        local pid=$!
        echo "  → Started with PID: $pid"
        return 0
    else
        echo "❌ JAR file not found: $jar_path"
        echo "   Available files in ${service_name}/target/:"
        ls -la "${service_name}/target/" 2>/dev/null || echo "   Target directory not found"
        return 1
    fi
}

# Start Discovery Service first
echo "🔍 Starting Discovery Service..."
if start_service "discovery-service" "8761"; then
    DISCOVERY_PID=$(jobs -p | tail -n 1)
    echo "⏳ Waiting for Discovery Service to start..."
    sleep 15
else
    echo "❌ Failed to start Discovery Service"
    exit 1
fi

# Start Auth Service
echo "🔐 Starting Auth Service..."
if start_service "auth-service" "8082"; then
    AUTH_PID=$(jobs -p | tail -n 1)
    echo "⏳ Waiting for Auth Service to start..."
    sleep 10
else
    echo "❌ Failed to start Auth Service"
    AUTH_PID=""
fi

# Start Book Service  
echo "📚 Starting Book Service..."
if start_service "book-service" "8083"; then
    BOOK_PID=$(jobs -p | tail -n 1)
    echo "⏳ Waiting for Book Service to start..."
    sleep 10
else
    echo "❌ Failed to start Book Service"
    BOOK_PID=""
fi

echo ""
echo "🎉 Services started successfully!"
echo "📍 Service URLs:"
echo "   🔍 Discovery Service: http://localhost:8761"
echo "   🔐 Auth Service: http://localhost:8082/api/auth/health"
echo "   📚 Book Service: http://localhost:8083/api/books/categories"
echo ""
echo "🌐 Infrastructure Services:"
echo "   🐘 PostgreSQL: localhost:5432"
echo "   🔴 Redis: localhost:6379"
echo "   🐰 RabbitMQ: localhost:5672 (Management: http://localhost:15672)"
echo ""

# Create PID list for killing services
PIDS=""
[ -n "$DISCOVERY_PID" ] && PIDS="$PIDS $DISCOVERY_PID"
[ -n "$AUTH_PID" ] && PIDS="$PIDS $AUTH_PID"
[ -n "$BOOK_PID" ] && PIDS="$PIDS $BOOK_PID"

if [ -n "$PIDS" ]; then
    echo "💡 To stop services, press Ctrl+C or run: kill$PIDS"
else
    echo "💡 No services started successfully"
    exit 1
fi
echo ""
echo "🌐 Open your frontend: file://$(pwd)/../index.html"

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "🛑 Stopping services..."
    if [ -n "$PIDS" ]; then
        kill $PIDS 2>/dev/null
        echo "✅ All services stopped"
    fi
    exit 0
}

# Set trap to cleanup on exit
trap cleanup SIGINT SIGTERM

echo "🔄 Services are running. Press Ctrl+C to stop all services."

# Keep script running
wait 