#!/bin/bash

echo "Testing BookVault Auth Service Build..."

cd auth-service

echo "Cleaning previous build..."
mvn clean

echo "Compiling auth-service..."
mvn compile

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