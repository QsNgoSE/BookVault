#!/bin/bash

# BookVault Free Deployment Setup Script
# This script helps prepare your application for free deployment

set -e

echo "🚀 BookVault Free Deployment Setup"
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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

# Check if Docker is installed
check_docker() {
    if command -v docker &> /dev/null; then
        print_status "Docker is installed"
    else
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
}

# Check if required directories exist
check_structure() {
    if [ -d "backend" ] && [ -d "backend/auth-service" ] && [ -d "backend/book-service" ]; then
        print_status "Project structure is correct"
    else
        print_error "Project structure is incorrect. Make sure you're in the BookVault root directory."
        exit 1
    fi
}

# Create simplified configuration files
create_config_files() {
    print_info "Creating deployment configuration files..."
    
    # Create railway-specific application.yml for auth service
    cat > backend/auth-service/src/main/resources/application-railway.yml << EOF
spring:
  application:
    name: bookvault-auth
  profiles:
    active: railway
  datasource:
    url: \${SPRING_DATASOURCE_URL}
    username: \${SPRING_DATASOURCE_USERNAME}
    password: \${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  main:
    lazy-initialization: true

server:
  port: \${SERVER_PORT:8080}

jwt:
  secret: \${JWT_SECRET}
  expiration: 86400000

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.bookvault: INFO
    org.springframework: WARN
    org.hibernate: WARN

# CORS configuration
cors:
  allowed-origins: \${CORS_ALLOWED_ORIGINS:http://localhost:3000,https://*.netlify.app,https://*.vercel.app}
EOF

    # Create railway-specific application.yml for book service
    cat > backend/book-service/src/main/resources/application-railway.yml << EOF
spring:
  application:
    name: bookvault-book
  profiles:
    active: railway
  datasource:
    url: \${SPRING_DATASOURCE_URL}
    username: \${SPRING_DATASOURCE_USERNAME}
    password: \${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  main:
    lazy-initialization: true

server:
  port: \${SERVER_PORT:8080}

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.bookvault: INFO
    org.springframework: WARN
    org.hibernate: WARN

# External service URLs
auth:
  service:
    url: \${AUTH_SERVICE_URL:http://localhost:8082}

# CORS configuration
cors:
  allowed-origins: \${CORS_ALLOWED_ORIGINS:http://localhost:3000,https://*.netlify.app,https://*.vercel.app}
EOF

    print_status "Configuration files created"
}

# Optimize Dockerfiles for free deployment
optimize_dockerfiles() {
    print_info "Optimizing Dockerfiles for free deployment..."
    
    # Optimize auth service Dockerfile
    cat > backend/auth-service/Dockerfile << EOF
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Copy parent POM if exists
COPY ../pom.xml ../pom.xml || true
COPY ../shared ./shared || true

# Download dependencies (for caching)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Use a smaller base image for runtime
FROM openjdk:17-jre-slim

# Add a user to run the application
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Set working directory
WORKDIR /app

# Copy the built application
COPY --from=0 /app/target/*.jar app.jar

# Change ownership
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Set JVM options for smaller memory footprint
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java \$JAVA_OPTS -jar app.jar"]
EOF

    # Optimize book service Dockerfile
    cat > backend/book-service/Dockerfile << EOF
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Copy parent POM if exists
COPY ../pom.xml ../pom.xml || true
COPY ../shared ./shared || true

# Download dependencies (for caching)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Use a smaller base image for runtime
FROM openjdk:17-jre-slim

# Add a user to run the application
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Set working directory
WORKDIR /app

# Copy the built application
COPY --from=0 /app/target/*.jar app.jar

# Change ownership
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Set JVM options for smaller memory footprint
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java \$JAVA_OPTS -jar app.jar"]
EOF

    print_status "Dockerfiles optimized"
}

# Test local build
test_build() {
    print_info "Testing local build..."
    
    cd backend
    
    if [ -f "pom.xml" ]; then
        print_info "Building parent project..."
        ./mvnw clean install -DskipTests -B
    fi
    
    print_info "Building auth service..."
    cd auth-service
    ./mvnw clean package -DskipTests -B
    
    cd ../book-service
    print_info "Building book service..."
    ./mvnw clean package -DskipTests -B
    
    cd ../..
    print_status "Local build successful"
}

# Create deployment checklist
create_checklist() {
    cat > DEPLOYMENT_CHECKLIST.md << EOF
# 📋 Deployment Checklist

## Pre-deployment
- [ ] Code committed to GitHub
- [ ] Docker builds successful locally
- [ ] Configuration files created
- [ ] Environment variables documented

## Railway Deployment
- [ ] Railway account created
- [ ] GitHub repository connected
- [ ] PostgreSQL database provisioned
- [ ] Auth service deployed
- [ ] Book service deployed
- [ ] Environment variables configured
- [ ] Health checks passing

## Frontend Deployment
- [ ] Netlify account created
- [ ] Frontend repository connected
- [ ] config.prod.js updated with backend URLs
- [ ] Build successful
- [ ] CORS configured properly

## Testing
- [ ] API endpoints responding
- [ ] Database connection working
- [ ] Frontend-backend communication working
- [ ] Authentication flow working
- [ ] Book listing/details working

## Post-deployment
- [ ] Domain configured (optional)
- [ ] SSL certificates working
- [ ] Performance monitoring set up
- [ ] Error logging configured

## Notes
- Backend URLs: 
  - Auth Service: https://your-auth-service.railway.app
  - Book Service: https://your-book-service.railway.app
- Frontend URL: https://your-frontend.netlify.app
- Database: Managed by Railway

## Troubleshooting Commands
\`\`\`bash
# Check Railway logs
railway logs

# Test API endpoints
curl https://your-auth-service.railway.app/actuator/health
curl https://your-book-service.railway.app/actuator/health

# Check CORS
curl -H "Origin: https://your-frontend.netlify.app" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS \
     https://your-auth-service.railway.app/api/auth/health
\`\`\`
EOF

    print_status "Deployment checklist created"
}

# Main execution
main() {
    echo ""
    print_info "Starting BookVault deployment preparation..."
    echo ""
    
    check_docker
    check_structure
    create_config_files
    optimize_dockerfiles
    
    print_warning "Skipping build test for now (uncomment if needed)"
    # test_build
    
    create_checklist
    
    echo ""
    print_status "Setup complete! 🎉"
    echo ""
    print_info "Next steps:"
    echo "1. 📖 Read DEPLOYMENT_GUIDE.md for detailed instructions"
    echo "2. 📋 Follow DEPLOYMENT_CHECKLIST.md step by step"
    echo "3. 🚀 Deploy to Railway + Netlify"
    echo "4. 🧪 Test your deployment"
    echo ""
    print_info "Files created:"
    echo "- docker-compose.simple.yml (simplified architecture)"
    echo "- config.prod.js (production frontend config)"
    echo "- DEPLOYMENT_GUIDE.md (detailed deployment instructions)"
    echo "- DEPLOYMENT_CHECKLIST.md (step-by-step checklist)"
    echo "- Optimized Dockerfiles for free deployment"
    echo "- Railway-specific application.yml files"
    echo ""
}

# Run main function
main 