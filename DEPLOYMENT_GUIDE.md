# BookVault Deployment Guide

This guide will help you deploy BookVault using Railway (recommended) and includes local development setup.

## 🛠️ Local Development Setup

### **Prerequisites**
- Java 17+
- PostgreSQL 12+
- Redis 7+ (for login attempt tracking)
- Maven 3.8+

### **Step 1: Install Dependencies**

**macOS:**
```bash
# Install using Homebrew
brew install postgresql@14 redis maven
brew services start postgresql@14
brew services start redis
```

**Ubuntu/Debian:**
```bash
# Install PostgreSQL and Redis
sudo apt update
sudo apt install postgresql postgresql-contrib redis-server maven
sudo systemctl start postgresql
sudo systemctl start redis-server
```

### **Step 2: Database Setup**
```bash
# Create database
sudo -u postgres psql
CREATE DATABASE bookvault;
CREATE USER bookvault WITH PASSWORD 'bookvault123';
GRANT ALL PRIVILEGES ON DATABASE bookvault TO bookvault;
\q
```

### **Step 3: Local Configuration**
Create `backend/auth-service/src/main/resources/application-local.yml`:
```yaml
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:postgresql://localhost:5432/bookvault
    username: bookvault
    password: bookvault123
    driver-class-name: org.postgresql.Driver
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8082

jwt:
  secret: your-very-long-secret-key-for-local-development-only
  expiration: 86400000

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### **Step 4: Run Services Locally**
```bash
# Terminal 1: Start Auth Service
cd backend/auth-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 2: Start Book Service
cd backend/book-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3: Start Frontend
cd ../../
python -m http.server 8080
# or
npx serve . -p 8080
```

---

## 🚀 Railway Deployment (Recommended)

### **Why Railway?**
- $5 monthly credit (enough for 2-3 services)
- PostgreSQL & Redis included
- Automatic deployments from Git
- Built-in health checks & monitoring
- Easy domain management

### **Step 1: Install Railway CLI**
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login
```

### **Step 2: Deploy Infrastructure**

#### **A. Create New Project**
```bash
# Create project
railway new bookvault-production
cd bookvault-production
```

#### **B. Deploy PostgreSQL Database**
```bash
# Add PostgreSQL service
railway add postgresql
railway deploy
```

#### **C. Deploy Redis Cache**
```bash
# Add Redis service
railway add redis
railway deploy
```

### **Step 3: Deploy Auth Service**

#### **A. Create Railway Config**
Create `backend/auth-service/railway.toml`:
```toml
[build]
builder = "NIXPACKS"
buildCommand = "mvn clean package -DskipTests -B"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 300
restartPolicyType = "ON_FAILURE"
restartPolicyMaxRetries = 3
startCommand = "java -Xmx450m -jar target/*.jar"

[env]
SPRING_PROFILES_ACTIVE = "railway"
SERVER_PORT = "8080"
```

#### **B. Deploy Auth Service**
```bash
# Deploy auth service
railway service create auth-service
railway up --service auth-service --detach
```

#### **C. Set Environment Variables**
```bash
# Set environment variables for auth service
railway variables set SPRING_PROFILES_ACTIVE=railway
railway variables set JWT_SECRET=your-very-long-and-secure-jwt-secret-key-here
railway variables set SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
railway variables set SPRING_REDIS_HOST=${{Redis.REDIS_HOST}}
railway variables set SPRING_REDIS_PORT=${{Redis.REDIS_PORT}}
railway variables set SPRING_REDIS_USERNAME=${{Redis.REDIS_USERNAME}}
railway variables set SPRING_REDIS_PASSWORD=${{Redis.REDIS_PASSWORD}}
```

### **Step 4: Deploy Book Service**

#### **A. Create Railway Config**
Create `backend/book-service/railway.toml`:
```toml
[build]
builder = "NIXPACKS"
buildCommand = "mvn clean package -DskipTests -B"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 300
restartPolicyType = "ON_FAILURE"
restartPolicyMaxRetries = 3
startCommand = "java -Xmx450m -jar target/*.jar"

[env]
SPRING_PROFILES_ACTIVE = "railway"
SERVER_PORT = "8080"
```

#### **B. Deploy Book Service**
```bash
# Deploy book service
railway service create book-service
railway up --service book-service --detach
```

#### **C. Set Environment Variables**
```bash
# Set environment variables for book service
railway variables set SPRING_PROFILES_ACTIVE=railway
railway variables set SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
railway variables set AUTH_SERVICE_URL=${{auth-service.RAILWAY_PUBLIC_DOMAIN}}
```

### **Step 5: Deploy Frontend (Netlify)**

#### **A. Prepare Frontend**
1. Update `config.js` with your Railway service URLs:
```javascript
const API_CONFIG = {
    AUTH_SERVICE_URL: 'https://your-auth-service.railway.app/api',
    BOOK_SERVICE_URL: 'https://your-book-service.railway.app/api',
    ORDER_SERVICE_URL: 'https://your-auth-service.railway.app/api',
    
    // Fallback for local development
    BASE_URL: 'https://your-auth-service.railway.app/api',
};
```

#### **B. Deploy to Netlify**
```bash
# Option 1: Drag & Drop
1. Build your frontend files
2. Drag the entire frontend folder to netlify.com/drop

# Option 2: Git Integration (Recommended)
1. Push frontend to GitHub
2. Connect GitHub to Netlify
3. Set build directory to root
4. Deploy
```

### **Step 6: Test Your Deployment**

#### **A. Health Check**
```bash
# Check auth service health
curl https://your-auth-service.railway.app/actuator/health

# Check book service health
curl https://your-book-service.railway.app/actuator/health
```

#### **B. Test Authentication**
```bash
# Test user registration
curl -X POST https://your-auth-service.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","firstName":"Test","lastName":"User"}'

# Test user login
curl -X POST https://your-auth-service.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

---

## 🔧 Railway CLI Commands Reference

### **Service Management**
```bash
# List all services
railway service list

# Switch to a service
railway service switch

# View service logs
railway logs

# View service metrics
railway metrics

# Connect to database
railway connect postgres
railway connect redis
```

### **Environment Variables**
```bash
# List all variables
railway variables

# Set a variable
railway variables set KEY=value

# Delete a variable
railway variables delete KEY

# Load variables from file
railway variables load .env
```

### **Deployments**
```bash
# Deploy current directory
railway up

# Deploy specific service
railway up --service auth-service

# View deployment status
railway status

# Rollback to previous deployment
railway rollback
```

---

## 🛠️ Quick Local Development

### **Using the Startup Script**
```bash
# Start all services at once
./start-local-dev.sh
```

### **Manual Startup**
```bash
# Terminal 1: Auth Service
cd backend/auth-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 2: Book Service
cd backend/book-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3: Frontend
python3 -m http.server 8080
```

### **Database Management**
```bash
# Connect to local PostgreSQL
psql -h localhost -U bookvault -d bookvault

# Reset database
DROP DATABASE bookvault;
CREATE DATABASE bookvault;
GRANT ALL PRIVILEGES ON DATABASE bookvault TO bookvault;
```

---

## 🔄 Alternative: Render Deployment

**Why Render?**
- 750 hours/month free tier per service
- Free PostgreSQL (90 days)
- Docker support

### **Step 1: Deploy Database**
```bash
# In Render dashboard:
1. Create new PostgreSQL database
2. Note connection details
```

### **Step 2: Deploy Services**
```bash
# For each service (Auth, Book):
1. Create new Web Service
2. Connect GitHub repo
3. Set Docker build
4. Add environment variables
5. Set health check endpoint: /actuator/health
```

---

## 🐳 Alternative: Google Cloud Run

**Why Cloud Run?**
- 2 million requests/month free
- Automatic scaling
- Container-based

### **Deploy Steps**
```bash
# 1. Build and push containers
docker build -t gcr.io/your-project/bookvault-auth ./backend/auth-service
docker push gcr.io/your-project/bookvault-auth

# 2. Deploy to Cloud Run
gcloud run deploy bookvault-auth \
  --image gcr.io/your-project/bookvault-auth \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

---

## 📊 Cost Comparison

| Platform | Services | Database | Total Cost |
|----------|----------|----------|------------|
| **Railway** | 2-3 services | PostgreSQL | FREE ($5 credit) |
| **Render** | 1-2 services | PostgreSQL (90 days) | FREE |
| **Google Cloud** | 2 services | Cloud SQL | ~$7-15/month |
| **Heroku** | 1 service | PostgreSQL | $7+/month |

---

## ⚙️ Production Configuration

### **Required Environment Variables**

**Auth Service:**
```bash
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=your_postgres_url
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your-very-long-secret-key
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.netlify.app
```

**Book Service:**
```bash
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=your_postgres_url
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
AUTH_SERVICE_URL=https://your-auth-service.railway.app
SERVER_PORT=8080
```

### **Database Schema**
Your services should auto-create tables with JPA, but you might need to:
```sql
-- Create databases if needed
CREATE DATABASE bookvault;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE bookvault TO your_username;
```

---

## 🔧 Troubleshooting

### **Common Issues:**

1. **Service Won't Start**
   - Check environment variables
   - Verify database connection
   - Check application logs

2. **CORS Errors**
   - Update CORS_ALLOWED_ORIGINS
   - Verify frontend domain in backend config

3. **Database Connection Issues**
   - Verify connection string format
   - Check firewall settings
   - Ensure database is running

4. **Memory Issues (Java)**
   - Add JVM options: `-Xmx512m -Xms256m`
   - Optimize Spring Boot settings

### **Optimization Tips:**

1. **Reduce Memory Usage**
   ```bash
   # Add to Dockerfile
   ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
   ```

2. **Faster Startup**
   ```bash
   # Add to application.yml
   spring:
     jpa:
       defer-datasource-initialization: true
     main:
       lazy-initialization: true
   ```

3. **Health Check Optimization**
   ```bash
   # Add to application.yml
   management:
     endpoint:
       health:
         show-details: always
     endpoints:
       web:
         exposure:
           include: health
   ```

---

## 📈 Scaling Strategy

**Phase 1: MVP (Current)**
- Frontend: Netlify
- Backend: 2 services on Railway
- Database: Railway PostgreSQL

**Phase 2: Growth**
- Add Order Service
- Implement caching (Redis)
- Add monitoring

**Phase 3: Production**
- Move to paid tiers
- Add API Gateway
- Implement full microservices

---

## 🎯 Next Steps

1. **Deploy MVP version** using Railway + Netlify
2. **Test all functionality** with real data
3. **Monitor resource usage** and costs
4. **Optimize** based on performance metrics
5. **Scale** as your user base grows

---

## 📞 Support

If you encounter issues:
1. Check the troubleshooting section
2. Review platform documentation
3. Check GitHub issues for similar problems
4. Consider upgrading to paid tiers for better support

---

**Happy Deploying! 🚀** 