#!/bin/bash

# BookVault Docker Build Script
echo "🐳 Building BookVault Docker Images..."

# Set correct JAVA_HOME for consistent builds
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
echo "🔧 JAVA_HOME set to: $JAVA_HOME"

# Change to backend directory
cd backend

# Build all services with Maven first
echo "📦 Building JAR files with Maven..."
mvn clean package -DskipTests

# Define services and their ports (compatible with all shells)
services="discovery-service:8761 config-service:8888 auth-service:8082 book-service:8083 order-service:8084 user-service:8085"

# Build Docker images for each service
for service_port in $services; do
    service=$(echo $service_port | cut -d: -f1)
    port=$(echo $service_port | cut -d: -f2)
    
    echo ""
    echo "🏗️  Building Docker image for $service (port $port)..."
    
    if [ -d "$service" ] && [ -f "$service/target/$service-1.0.0.jar" ]; then
        cd "$service"
        docker build -t "bookvault/$service:latest" .
        if [ $? -eq 0 ]; then
            echo "✅ Successfully built bookvault/$service:latest"
        else
            echo "❌ Failed to build $service"
        fi
        cd ..
    else
        echo "⚠️  Skipping $service - JAR file not found or service directory missing"
        if [ ! -d "$service" ]; then
            echo "   📁 Directory $service does not exist"
        elif [ ! -f "$service/target/$service-1.0.0.jar" ]; then
            echo "   📦 JAR file $service/target/$service-1.0.0.jar not found"
        fi
    fi
done

echo ""
echo "🎉 Docker build process completed!"
echo ""
echo "📋 Built Images:"
docker images | grep bookvault

echo ""
echo "🚀 To run the services:"
echo "   docker-compose up -d"
echo ""
echo "🔍 To view running containers:"
echo "   docker ps" 