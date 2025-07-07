# 📋 BookVault Deployment Checklist - Streamlined 2-Service Architecture

## ✅ Pre-Deployment Setup
- [ ] GitHub repository created and code pushed
- [ ] Railway account created and CLI installed
- [ ] Local development environment working
- [ ] Both services build successfully locally

## 🏗️ Architecture Overview

**Streamlined 2-Service Design:**
- **Auth Service** (Port 8082): Authentication + User Management + Admin Operations
- **Book Service** (Port 8083): Books + Categories + Reviews + **Orders + Shopping Cart**

## 🚀 Railway Backend Deployment

### Infrastructure Setup
- [ ] Railway project created: `railway new bookvault-production`
- [ ] PostgreSQL database added: `railway add postgresql`
- [ ] Redis cache added: `railway add redis`
- [ ] Database connection tested

### Auth Service Deployment
- [ ] Auth service created: `railway service create auth-service`
- [ ] Auth service deployed: `railway up --service auth-service`
- [ ] Environment variables configured:
  - [ ] `SPRING_PROFILES_ACTIVE=railway`
  - [ ] `JWT_SECRET=your-secure-secret-key-here`
  - [ ] `DATABASE_URL=postgresql://...` (auto-configured)
  - [ ] `REDIS_URL=redis://...` (auto-configured)
- [ ] Service health check passed
- [ ] Auth endpoints tested:
  - [ ] POST `/api/auth/register`
  - [ ] POST `/api/auth/login`
  - [ ] GET `/api/auth/profile/{userId}`

### Book Service Deployment (Including Orders)
- [ ] Book service created: `railway service create book-service`
- [ ] Book service deployed: `railway up --service book-service`
- [ ] Environment variables configured:
  - [ ] `SPRING_PROFILES_ACTIVE=railway`
  - [ ] `JWT_SECRET=your-secure-secret-key-here` (same as auth)
  - [ ] `DATABASE_URL=postgresql://...` (shared database)
- [ ] Service health check passed
- [ ] Book endpoints tested:
  - [ ] GET `/api/books`
  - [ ] GET `/api/books/{id}`
  - [ ] GET `/api/categories`
- [ ] **Order endpoints tested:**
  - [ ] POST `/api/orders`
  - [ ] GET `/api/orders/my-orders`
  - [ ] PUT `/api/orders/{id}/cancel`

### Service URLs Documentation
- [ ] Auth Service URL: `https://auth-service-xxx.railway.app`
- [ ] Book Service URL: `https://book-service-xxx.railway.app`

## 🌐 Frontend GitHub Pages Deployment

### Repository Setup
- [ ] Frontend repository ready for GitHub Pages
- [ ] `config.js` updated with Railway service URLs
- [ ] All frontend files in root or docs folder

### GitHub Pages Configuration
- [ ] Repository → Settings → Pages
- [ ] Source: Deploy from branch
- [ ] Branch: main, Folder: / (root)
- [ ] Custom domain configured (optional)

### Frontend Configuration Update
```javascript
// config.js - Update these URLs after Railway deployment
const BookVaultConfig = {
    production: {
        AUTH_SERVICE_URL: 'https://your-auth-service.railway.app/api',
        BOOK_SERVICE_URL: 'https://your-book-service.railway.app/api',
        ORDER_SERVICE_URL: 'https://your-book-service.railway.app/api', // Same as book service
    }
}
```

## 🔧 Post-Deployment Testing

### Authentication Flow
- [ ] User registration works
- [ ] User login works
- [ ] JWT token validation works
- [ ] User profile updates work
- [ ] Admin panel accessible

### Book Management
- [ ] Book catalog loads
- [ ] Book search works
- [ ] Book details display
- [ ] Categories filter properly

### **Order Management (New!)**
- [ ] Shopping cart functionality
- [ ] Order creation works
- [ ] Order history displays
- [ ] Order status updates work
- [ ] Order cancellation works
- [ ] Stock management works (inventory decreases)

### Admin Functions
- [ ] User management works
- [ ] Book management works
- [ ] **Order management works**
- [ ] **Order status updates work**
- [ ] **Order tracking works**

## 🐛 Troubleshooting

### Common Issues
- [ ] **Database tables created**: Both auth and book services share one database
- [ ] **JWT secret consistency**: Same secret across both services
- [ ] **CORS enabled**: Both services can be called from frontend
- [ ] **Order tables created**: New order tables exist in shared database

### Database Schema Check
```sql
-- Verify all tables exist:
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public';

-- Should include:
-- users, books, categories, book_categories, book_reviews
-- orders, order_items (NEW)
```

## 📊 Service Architecture Summary

### Auth Service Responsibilities:
- ✅ User authentication (register, login, JWT)
- ✅ User profile management
- ✅ Admin user management
- ✅ Login attempt tracking
- ✅ Role-based access control

### Book Service Responsibilities:
- ✅ Book catalog management
- ✅ Category management
- ✅ Book reviews
- ✅ **Order processing** (NEW)
- ✅ **Shopping cart** (NEW)
- ✅ **Order tracking** (NEW)
- ✅ **Inventory management** (NEW)

## 💰 Cost Optimization Benefits

**2-Service Architecture Advantages:**
- **50% fewer Railway services** = Lower monthly costs
- **Shared database** = Single PostgreSQL instance
- **Simplified deployment** = Easier maintenance
- **Better performance** = Fewer network calls between services
- **Complete functionality** = All features preserved

## 🔄 Deployment Commands Summary

```bash
# 1. Infrastructure
railway new bookvault-production
railway add postgresql redis

# 2. Auth Service
railway service create auth-service
cd backend/auth-service
railway up --service auth-service

# 3. Book Service (with Orders)
railway service create book-service  
cd backend/book-service
railway up --service book-service

# 4. Update Frontend URLs and deploy to GitHub Pages
```

## ✅ Final Checklist
- [ ] Both services deployed and healthy
- [ ] Database tables created
- [ ] Frontend updated with correct URLs
- [ ] All core functionality tested
- [ ] **Order functionality tested**
- [ ] Production environment ready
- [ ] Monitoring configured
- [ ] Documentation updated

---

**🎉 Success Criteria:** 
- Users can register, login, browse books, **place orders, and track order status**
- Admins can manage users, books, **and orders**
- All functionality works end-to-end
- **Shopping cart and order management fully functional**

## 📊 Service URLs Template

**Backend Services (Railway):**
- Auth Service: `https://bookvault-auth-production.up.railway.app`
- Book Service: `https://bookvault-book-production.up.railway.app`
- Database: Managed by Railway PostgreSQL
- Cache: Managed by Railway Redis

**Frontend (GitHub Pages):**
- Website: `https://yourusername.github.io/repository-name`
- Custom Domain: `https://your-custom-domain.com` (optional)

## 📞 Support Resources

- **Railway Documentation**: https://docs.railway.app/
- **GitHub Pages Documentation**: https://docs.github.com/en/pages
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Railway Community**: https://railway.app/discord
- **GitHub Community**: https://github.com/community 