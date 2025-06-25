#!/bin/bash

echo "🔍 BookVault Backend Version Verification"
echo "========================================"
echo ""

# Check Java version
echo "☕ Java Version:"
java -version 2>&1 | head -n 1
echo ""

# Check Maven version
echo "📦 Maven Version:"
mvn --version 2>&1 | head -n 1
echo ""

# Check parent pom.xml versions
echo "🏗️ Parent POM Configuration:"
echo "Java Version: $(grep -A1 '<java.version>' pom.xml | grep -o '[0-9]*')"
echo "Spring Boot: $(grep -A1 '<spring-boot.version>' pom.xml | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+')"
echo "Spring Cloud: $(grep -A1 '<spring-cloud.version>' pom.xml | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+')"
echo "JWT Version: $(grep -A1 '<jjwt.version>' pom.xml | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+')"
echo ""

# Check Dockerfile versions
echo "🐳 Docker Image Versions:"
for service in auth-service book-service discovery-service config-service order-service user-service; do
    if [ -f "$service/Dockerfile" ]; then
        java_version=$(grep "FROM eclipse-temurin" "$service/Dockerfile" | grep -o '[0-9]\+')
        echo "$service: Java $java_version"
    fi
done
echo ""

# Check for version conflicts
echo "🔍 Version Conflict Check:"
echo ""

# Check Docker vs Maven Java version
maven_java=$(grep -A1 '<java.version>' pom.xml | grep -o '[0-9]*')
docker_java=$(grep "FROM eclipse-temurin" auth-service/Dockerfile | grep -o '[0-9]\+')

if [ "$maven_java" = "$docker_java" ]; then
    echo "✅ Java versions match: Maven $maven_java = Docker $docker_java"
else
    echo "❌ Java version mismatch: Maven $maven_java ≠ Docker $docker_java"
fi

# Check if all Dockerfiles have same Java version
echo ""
echo "🐳 Docker Consistency Check:"
inconsistent=false
for service in auth-service book-service discovery-service config-service order-service user-service; do
    if [ -f "$service/Dockerfile" ]; then
        java_version=$(grep "FROM eclipse-temurin" "$service/Dockerfile" | grep -o '[0-9]\+')
        if [ "$java_version" != "$docker_java" ]; then
            echo "❌ $service uses Java $java_version (expected $docker_java)"
            inconsistent=true
        fi
    fi
done

if [ "$inconsistent" = false ]; then
    echo "✅ All Docker images use Java $docker_java"
fi

echo ""
echo "🎯 Version Summary:"
echo "==================="
echo "Standard Java: $maven_java"
echo "Docker Images: $docker_java"
echo "Spring Boot: $(grep -A1 '<spring-boot.version>' pom.xml | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+')"
echo "Status: $(if [ "$maven_java" = "$docker_java" ] && [ "$inconsistent" = false ]; then echo "✅ All versions consistent"; else echo "❌ Version conflicts detected"; fi)"
echo ""

# Quick build test
echo "🚀 Quick Build Test:"
echo "===================="
echo "Running: mvn clean compile -q"
if mvn clean compile -q > /dev/null 2>&1; then
    echo "✅ Build successful - versions are compatible"
else
    echo "❌ Build failed - check version compatibility"
    echo "Run 'mvn clean compile' for detailed error messages"
fi

echo ""
echo "📖 For detailed version info, see: VERSION_MATRIX.md" 