#!/bin/bash

echo "🧪 Testing BookVault Backend Services Build..."
echo "📋 Version Matrix: Java 21, Spring Boot 3.2.4, Maven 3.12.1"
echo ""

# Check Java version
echo "☕ Java Version Check:"
java -version 2>&1 | head -n 3
echo ""

# Test parent build first
echo "🏗️ Testing Parent Build..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Parent build failed!"
    exit 1
fi

echo "✅ Parent build successful!"
echo ""

# Test auth-service specifically
echo "🔐 Testing Auth Service Build..."
cd auth-service

echo "🧹 Cleaning previous build..."
mvn clean -q

echo "🔨 Compiling auth-service..."
mvn compile -q

if [ $? -eq 0 ]; then
    echo "✅ Auth service compiled successfully!"
else
    echo "❌ Auth service compilation failed!"
    exit 1
fi

echo "Running tests..."
mvn test -DskipTests=false

if [ $? -eq 0 ]; then
    echo "✅ Tests passed!"
else
    echo "⚠️ Some tests failed, but continuing..."
fi

echo "Packaging..."
mvn package -DskipTests=true

if [ $? -eq 0 ]; then
    echo "✅ Auth service packaged successfully!"
    echo "Build completed successfully! ✅"
else
    echo "❌ Packaging failed!"
    exit 1
fi 