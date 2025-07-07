#!/bin/bash

# BookVault Free Deployment Setup Script
# This script helps prepare your application for free deployment

set -e

RAILWAY_TOKEN=669be141-8476-4cda-a158-c66ffa6a1967
AUTH_SERVICE_URL=""
BOOK_SERVICE_URL=""

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

# Check if Railway CLI is installed
check_railway_cli() {
    if command -v railway &> /dev/null; then
        print_status "Railway CLI is installed"
    else
        print_info "Installing Railway CLI..."
        # Install Railway CLI
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            brew install railway
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            curl -fsSL https://railway.app/install.sh | sh
        else
            print_error "Please install Railway CLI manually from https://railway.app/cli"
            exit 1
        fi
        print_status "Railway CLI installed"
    fi
}

# Login to Railway
login_railway() {
    print_info "Checking Railway login status..."
    
    # Check if already logged in
    if railway whoami &>/dev/null; then
        print_status "Already logged in to Railway"
    else
        print_error "Not logged in to Railway. Please run 'railway login' first"
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
    url: \${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
  data:
    redis:
      url: \${REDIS_URL}
      host: \${REDIS_HOST:localhost}
      port: \${REDIS_PORT:6379}
      password: \${REDIS_PASSWORD:}
      timeout: 60000
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
  port: \${PORT:8080}

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
    url: \${DATABASE_URL}
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
  port: \${PORT:8080}

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

# Create Railway buildpack configuration (no Docker needed)
create_railway_buildpacks_config() {
    print_info "Creating Railway buildpack configuration..."
    
    # Create .railwayignore for auth service
    cat > backend/auth-service/.railwayignore << EOF
target/
*.log
.DS_Store
.git/
.mvn/
Dockerfile*
docker-compose*
EOF

    # Create .railwayignore for book service
    cat > backend/book-service/.railwayignore << EOF
target/
*.log
.DS_Store
.git/
.mvn/
Dockerfile*
docker-compose*
EOF

    # Copy parent POM and shared module to auth service  
    cp backend/pom.xml backend/auth-service/parent-pom.xml
    cp -r backend/shared backend/auth-service/
    
    # Create Railway nixpacks.toml for auth service
    cat > backend/auth-service/nixpacks.toml << EOF
[phases.build]
cmds = [
    "mvn install:install-file -Dfile=parent-pom.xml -DgroupId=com.bookvault -DartifactId=bookvault-backend -Dversion=1.0.0 -Dpackaging=pom",
    "cd shared && mvn clean install -DskipTests -B && cd ..",
    "mvn clean package -DskipTests -B"
]

[start]
cmd = "java -Dserver.port=\$PORT -jar target/*.jar"
EOF

    # Copy parent POM and shared module to book service
    cp backend/pom.xml backend/book-service/parent-pom.xml
    cp -r backend/shared backend/book-service/
    
    # Create Railway nixpacks.toml for book service
    cat > backend/book-service/nixpacks.toml << EOF
[phases.build]
cmds = [
    "mvn install:install-file -Dfile=parent-pom.xml -DgroupId=com.bookvault -DartifactId=bookvault-backend -Dversion=1.0.0 -Dpackaging=pom",
    "cd shared && mvn clean install -DskipTests -B && cd ..",
    "mvn clean package -DskipTests -B"
]

[start]
cmd = "java -Dserver.port=\$PORT -jar target/*.jar"
EOF

    print_status "Railway buildpack configuration created"
}



# Test local build
test_build() {
    print_info "Testing local build..."
    
    cd backend
    
    # Make Maven wrapper executable
    chmod +x mvnw
    
    if [ -f "pom.xml" ]; then
        print_info "Building parent project..."
        ./mvnw clean install -DskipTests -B
    fi
    
    print_info "Building auth service..."
    cd auth-service
    chmod +x mvnw
    ./mvnw clean package -DskipTests -B
    
    cd ../book-service
    print_info "Building book service..."
    chmod +x mvnw
    ./mvnw clean package -DskipTests -B
    
    cd ../..
    print_status "Local build successful"
}

# Create Railway project
create_railway_project() {
    print_info "Linking to existing Railway project..."
    
    # Link to existing project
    railway link -p f8ab2f01-dfb1-49e8-ad20-8135585bb276
    
    # Add PostgreSQL service
    print_info "Adding PostgreSQL service..."
    railway add -d postgres
    
    # Add Redis service for authentication caching
    print_info "Adding Redis service..."
    railway add -d redis
    
    print_status "Railway project linked with database services"
}

# Deploy Auth Service
deploy_auth_service() {
    print_info "Deploying Auth Service..."
    
    cd backend/auth-service
    
    # Link to the project and create service
    railway link -p f8ab2f01-dfb1-49e8-ad20-8135585bb276
    
    # Create auth service
    print_info "Creating auth service..."
    railway add -s auth-service
    
    # Set environment variables
    railway variables --set "SPRING_PROFILES_ACTIVE=railway"
    railway variables --set "JWT_SECRET=$(openssl rand -base64 32)"
    railway variables --set "CORS_ALLOWED_ORIGINS=*"
    
    # Redis will be automatically connected by Railway
    # DATABASE_URL and REDIS_URL will be injected automatically
    
    # Deploy the service
    railway up --detach
    
    # Wait for deployment
    print_info "Waiting for auth service to deploy..."
    sleep 60
    
    # Get service URL
    AUTH_SERVICE_URL=$(railway status --json | jq -r '.deployments[0].url // empty')
    
    if [ -z "$AUTH_SERVICE_URL" ]; then
        print_warning "Could not get auth service URL automatically"
        print_info "Please check Railway dashboard for the URL"
        AUTH_SERVICE_URL="https://auth-service-production.up.railway.app"
    fi
    
    echo "Auth Service URL: $AUTH_SERVICE_URL"
    
    cd ../..
    print_status "Auth Service deployed"
}

# Deploy Book Service
deploy_book_service() {
    print_info "Deploying Book Service..."
    
    cd backend/book-service
    
    # Link to the project and create service
    railway link -p f8ab2f01-dfb1-49e8-ad20-8135585bb276
    
    # Create book service
    print_info "Creating book service..."
    railway add -s book-service
    
    # Set environment variables
    railway variables --set "SPRING_PROFILES_ACTIVE=railway"
    railway variables --set "AUTH_SERVICE_URL=$AUTH_SERVICE_URL"
    railway variables --set "CORS_ALLOWED_ORIGINS=*"
    
    # Deploy the service
    railway up --detach
    
    # Wait for deployment
    print_info "Waiting for book service to deploy..."
    sleep 60
    
    # Get service URL
    BOOK_SERVICE_URL=$(railway status --json | jq -r '.deployments[0].url // empty')
    
    if [ -z "$BOOK_SERVICE_URL" ]; then
        print_warning "Could not get book service URL automatically"
        print_info "Please check Railway dashboard for the URL"
        BOOK_SERVICE_URL="https://book-service-production.up.railway.app"
    fi
    
    echo "Book Service URL: $BOOK_SERVICE_URL"
    
    cd ../..
    print_status "Book Service deployed"
}

# Configure database
configure_database() {
    print_info "Configuring database..."
    
    # The PostgreSQL database is already provisioned with the template
    # We just need to ensure both services can access it
    
    print_status "Database configured"
}

# Update frontend config
update_frontend_config() {
    print_info "Updating frontend configuration..."
    
    # Create production config
    cat > config.prod.js << EOF
window.BookVaultConfig = {
    environment: 'production',
    production: {
        AUTH_SERVICE_URL: '$AUTH_SERVICE_URL/api',
        BOOK_SERVICE_URL: '$BOOK_SERVICE_URL/api'
    }
};
EOF
    
    print_status "Frontend configuration updated"
}

# Check deployment health
check_deployment_health() {
    print_info "Checking deployment health..."
    
    # Wait a bit for services to fully start
    sleep 60
    
    # Check auth service health
    if curl -f "$AUTH_SERVICE_URL/actuator/health" &>/dev/null; then
        print_status "Auth service is healthy"
    else
        print_warning "Auth service health check failed"
    fi
    
    # Check book service health
    if curl -f "$BOOK_SERVICE_URL/actuator/health" &>/dev/null; then
        print_status "Book service is healthy"
    else
        print_warning "Book service health check failed"
    fi
}

# Create deployment checklist
create_checklist() {
    cat > DEPLOYMENT_CHECKLIST.md << EOF
# 📋 Deployment Checklist

## Pre-deployment
- [ ] Code committed to GitHub
- [ ] Railway configuration files created
- [ ] Environment variables documented
- [ ] Redis configuration verified

## Railway Deployment
- [ ] Railway account created
- [ ] GitHub repository connected (optional)
- [ ] PostgreSQL database provisioned
- [ ] Redis service provisioned
- [ ] Auth service deployed (with Redis connection)
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
- Database: PostgreSQL (managed by Railway)
- Cache: Redis (managed by Railway)
- Authentication uses Redis for session management

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
    print_info "Starting BookVault Backend Deployment to Railway..."
    echo ""
    
    # Pre-deployment checks
    check_railway_cli
    check_structure
    
    # Login to Railway
    login_railway
    
    # Prepare deployment files
    create_config_files
    create_railway_buildpacks_config
    
    # Create Railway project and deploy services
    create_railway_project
    configure_database
    deploy_auth_service
    deploy_book_service
    
    # Update frontend configuration
    update_frontend_config
    
    # Health checks
    check_deployment_health
    
    # Create deployment documentation
    create_checklist
    
    echo ""
    print_status "🎉 Deployment Complete!"
    echo ""
    print_info "📋 Deployment Summary:"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🔐 Auth Service: $AUTH_SERVICE_URL"
    echo "📚 Book Service: $BOOK_SERVICE_URL"
    echo "🗄️  Database: PostgreSQL (managed by Railway)"
    echo "🔴 Cache: Redis (managed by Railway)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    print_info "🧪 Test your API:"
    echo "curl $AUTH_SERVICE_URL/actuator/health"
    echo "curl $BOOK_SERVICE_URL/actuator/health"
    echo ""
    print_info "📱 Frontend Configuration:"
    echo "✓ config.prod.js created with backend URLs"
    echo "✓ Deploy your frontend to Netlify/Vercel"
    echo "✓ Use config.prod.js for production builds"
    echo ""
    print_info "📋 Next Steps:"
    echo "1. 🧪 Test your backend APIs"
    echo "2. 📱 Deploy frontend to Netlify/Vercel"
    echo "3. 🔄 Update CORS settings if needed"
    echo "4. 📖 Check DEPLOYMENT_CHECKLIST.md"
    echo ""
    print_warning "⚠️  Important Notes:"
    echo "• Railway free tier has usage limits"
    echo "• Services may sleep after inactivity"
    echo "• Check Railway dashboard for logs and metrics"
    echo ""
}

# Run main function
main 