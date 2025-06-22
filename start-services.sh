#!/bin/bash

echo "🚀 Starting BookVault Backend Services..."

# Set correct JAVA_HOME to ensure Maven uses the right Java version
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
echo "🔧 JAVA_HOME set to: $JAVA_HOME"

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    echo "Install with: brew install openjdk@17"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1-2)
echo "✅ Java version: $(java -version 2>&1 | head -n 1)"
# Java version check is optional since we support Java 17+

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

echo "⚠️  Build failed. Let's try to start only the services that built successfully..."

# Start Discovery Service
if start_service "discovery-service" "8761"; then
    DISCOVERY_PID=$(jobs -p | tail -n 1)
else
    echo "❌ Failed to start Discovery Service"
    DISCOVERY_PID=""
fi

# Wait a bit for discovery service to start
if [ -n "$DISCOVERY_PID" ]; then
    echo "⏳ Waiting for Discovery Service to start..."
    sleep 10
fi

# Start Auth Service
if start_service "auth-service" "8082"; then
    AUTH_PID=$(jobs -p | tail -n 1)
else
    echo "❌ Failed to start Auth Service"
    AUTH_PID=""
fi

# Start Book Service  
if start_service "book-service" "8083"; then
    BOOK_PID=$(jobs -p | tail -n 1)
else
    echo "❌ Failed to start Book Service"
    BOOK_PID=""
fi

echo ""
echo "🎉 All services starting!"
echo "📍 Service URLs:"
echo "   Discovery Service: http://localhost:8761"
echo "   Auth Service: http://localhost:8082/api/auth/health"
echo "   Book Service: http://localhost:8083/api/books/categories"
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
fi
echo ""
echo "🌐 Open your frontend: file://$(pwd)/../index.html"

# Keep script running
wait 