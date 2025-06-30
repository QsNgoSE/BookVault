# BookVault Free Deployment Guide

This guide will help you deploy BookVault for free using the most cost-effective platforms.

## 🚀 Quick Start: Railway Deployment (Recommended)

**Why Railway?**
- $5 monthly credit (enough for 2-3 services)
- PostgreSQL included
- Automatic deployments from Git
- Easy container deployments

### **Step 1: Prepare Your Code**

1. **Simplify your architecture** (already created: `docker-compose.simple.yml`)
2. **Update frontend config** to use production URLs (already created: `config.prod.js`)

### **Step 2: Deploy Backend Services**

#### **A. Create Railway Account**
1. Sign up at [railway.app](https://railway.app)
2. Connect your GitHub account
3. Fork or push your BookVault repo to GitHub

#### **B. Deploy PostgreSQL Database**
```bash
# In Railway dashboard:
1. Click "New Project"
2. Select "Provision PostgreSQL"
3. Note the connection details
```

#### **C. Deploy Auth Service**
```bash
# In Railway dashboard:
1. Click "New Service"
2. Select "GitHub Repo"
3. Choose your BookVault repo
4. Set Root Directory: backend/auth-service
5. Add Environment Variables:
   - SPRING_PROFILES_ACTIVE=railway
   - SPRING_DATASOURCE_URL=[Your PostgreSQL URL from step B]
   - SPRING_DATASOURCE_USERNAME=[From PostgreSQL service]
   - SPRING_DATASOURCE_PASSWORD=[From PostgreSQL service]
   - JWT_SECRET=your-secret-key-here-make-it-long-and-secure
   - SERVER_PORT=8080
```

#### **D. Deploy Book Service**
```bash
# In Railway dashboard:
1. Click "New Service"
2. Select "GitHub Repo"
3. Choose your BookVault repo
4. Set Root Directory: backend/book-service
5. Add Environment Variables:
   - SPRING_PROFILES_ACTIVE=railway
   - SPRING_DATASOURCE_URL=[Your PostgreSQL URL]
   - SPRING_DATASOURCE_USERNAME=[From PostgreSQL service]
   - SPRING_DATASOURCE_PASSWORD=[From PostgreSQL service]
   - AUTH_SERVICE_URL=[URL from Auth service deployment]
   - SERVER_PORT=8080
```

### **Step 3: Deploy Frontend (Netlify)**

#### **A. Prepare Frontend**
1. Copy `config.prod.js` content to `config.js`
2. Update API URLs with your Railway service URLs

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