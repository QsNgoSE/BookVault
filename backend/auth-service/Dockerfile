FROM eclipse-temurin:17-jre-jammy

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY target/auth-service-*.jar app.jar

# Expose port
EXPOSE ${PORT:-8082}

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8082}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 