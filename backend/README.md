# BookVault Backend - Simplified Architecture

This is the simplified, production-ready backend implementation for BookVault, optimized for Railway deployment.

## 🏗️ Architecture Overview

The backend has been refactored from a complex microservices architecture to a simplified, efficient structure:

```
BookVault Backend (Simplified)
├── auth-service/          # Authentication & User Management
├── book-service/          # Book Catalog & Orders
├── shared/               # Shared libraries and utilities
└── infrastructure/       # (Optional) Discovery & Config services
```

## 🎯 Core Services

### 1. Auth Service (Port: 8082)
**Purpose**: Complete authentication and user management
- ✅ JWT token generation and validation
- ✅ User registration and login
- ✅ Profile management
- ✅ Role-based authorization (USER, SELLER, ADMIN)
- ✅ Redis-based login attempt tracking
- ✅ Account banning (temporary and permanent)
- ✅ Admin user management

### 2. Book Service (Port: 8083)
**Purpose**: Complete book catalog and order management
- ✅ Book catalog management
- ✅ Categories and search functionality
- ✅ Book reviews and ratings
- ✅ Inventory management
- ✅ Basic order processing
- ✅ Shopping cart functionality

### 3. Shared Module
**Purpose**: Common utilities and DTOs
- ✅ JWT utilities
- ✅ Exception handling
- ✅ Validation utilities
- ✅ Common DTOs and enums

## 📋 Removed/Simplified Services

### ❌ User Service
- **Reason**: Functionality merged into auth-service
- **Impact**: Simplified user management, reduced complexity

### ❌ Order Service  
- **Reason**: Basic order functionality moved to book-service
- **Impact**: Simpler deployment, adequate for MVP

### ❌ Discovery Service (Eureka)
- **Reason**: Not needed for Railway deployment
- **Impact**: Reduced infrastructure complexity

### ❌ Config Service
- **Reason**: Using Spring profiles instead
- **Impact**: Simpler configuration management

## 🚀 Technology Stack

- **Java**: 17+
- **Spring Boot**: 3.2+
- **Spring Security**: 6.x with JWT
- **Spring Data JPA**: Hibernate 6.x
- **Database**: PostgreSQL 14+
- **Cache**: Redis 7+ (for login attempts)
- **Documentation**: OpenAPI 3 (Swagger)
- **Build Tool**: Maven 3.8+

## 🌍 Deployment Profiles

### 1. Local Development
```yaml
spring.profiles.active: local
- PostgreSQL: localhost:5432
- Redis: localhost:6379
- Verbose logging and debugging
```

### 2. Railway Production
```yaml
spring.profiles.active: railway
- PostgreSQL: Railway managed
- Redis: Railway managed
- Optimized for cloud deployment
```

### 3. Docker (Optional)
```yaml
spring.profiles.active: docker
- For local container testing only
- Not recommended for production
```

## 📊 Database Design

### Auth Service Database (`bookvault`)
```sql
-- Users table
users (id, email, password, first_name, last_name, role, created_at, updated_at)

-- Future: Address, preferences, etc.
```

### Book Service Database (`bookvault`) 
```sql
-- Books and catalog
books (id, title, author, description, price, stock, created_at)
categories (id, name, description)
book_categories (book_id, category_id)
book_reviews (id, book_id, user_id, rating, comment, created_at)

-- Basic orders (simplified)
orders (id, user_id, total_amount, status, created_at)
order_items (id, order_id, book_id, quantity, price)
```

## 🔒 Security Features

### Authentication & Authorization
- ✅ JWT-based stateless authentication
- ✅ Role-based access control (USER, SELLER, ADMIN)
- ✅ Password encryption with BCrypt
- ✅ CORS configuration for frontend integration

### Login Attempt Protection
- ✅ Redis-based attempt tracking
- ✅ Temporary ban: 3 failed attempts = 15 min ban
- ✅ Permanent ban: 5 failed attempts = database ban
- ✅ IP-based banning: 5 attempts = 30 min ban
- ✅ Graceful fallback when Redis unavailable

### API Security
- ✅ Input validation and sanitization
- ✅ SQL injection prevention
- ✅ XSS protection
- ✅ Rate limiting ready

## 📈 Performance Optimizations

### Database
- ✅ Connection pooling
- ✅ Lazy loading optimization
- ✅ Proper indexing
- ✅ Query optimization

### Caching
- ✅ Redis for login attempts
- ✅ Application-level caching ready
- ✅ Database query caching

### Memory Management
- ✅ Optimized for Railway's 512MB limit
- ✅ Lazy initialization
- ✅ Efficient logging configuration

## 🛠️ Development Workflow

### Local Development
```bash
# Start dependencies
brew services start postgresql@14 redis

# Start services
./start-local-dev.sh

# Or manually:
cd backend/auth-service && mvn spring-boot:run -Dspring-boot.run.profiles=local
cd backend/book-service && mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Railway Deployment
```bash
# Deploy auth service
railway service create auth-service
railway up --service auth-service

# Deploy book service  
railway service create book-service
railway up --service book-service
```

### Testing
```bash
# Run all tests
cd backend && mvn test

# Test specific service
cd backend/auth-service && mvn test
```

## 🔍 API Documentation

### Auth Service APIs
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile
- `PUT /api/auth/profile` - Update user profile
- `GET /api/admin/users` - Admin: List users
- `PUT /api/admin/users/{id}` - Admin: Update user

### Book Service APIs
- `GET /api/books` - List books with search/filter
- `GET /api/books/{id}` - Get book details
- `POST /api/books` - Create book (SELLER/ADMIN)
- `PUT /api/books/{id}` - Update book (SELLER/ADMIN)
- `POST /api/books/{id}/reviews` - Add review
- `GET /api/categories` - List categories

### Health & Monitoring
- `GET /actuator/health` - Health check
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Application metrics

## 📦 Build & Deployment

### Maven Build
```bash
# Build all services
cd backend && mvn clean package

# Build specific service
cd backend/auth-service && mvn clean package
```

### Docker Build (Optional)
```bash
# Build auth service
cd backend/auth-service && docker build -t bookvault-auth .

# Build book service
cd backend/book-service && docker build -t bookvault-book .
```

### Railway Deployment
```bash
# Deploy with Railway CLI
railway login
railway up --service auth-service
railway up --service book-service
```

## 🔄 Migration from Complex Architecture

### What Was Removed
1. **Eureka Discovery**: Not needed for Railway
2. **Spring Cloud Config**: Replaced with profiles
3. **Separate User Service**: Merged into auth-service
4. **Complex Order Service**: Simplified into book-service
5. **API Gateway**: Not needed for two services

### What Was Kept
1. **JWT Authentication**: Stateless and scalable
2. **Redis Login Tracking**: Essential security feature
3. **Role-based Authorization**: Business requirement
4. **PostgreSQL**: Reliable database choice
5. **Shared Module**: Code reusability

### Benefits of Simplification
- ✅ **Reduced Complexity**: Easier to maintain
- ✅ **Lower Costs**: Fewer services = lower Railway costs
- ✅ **Faster Development**: Less moving parts
- ✅ **Better Performance**: Reduced network calls
- ✅ **Simpler Deployment**: Fewer dependencies
- ✅ **Easier Debugging**: Centralized logging

## 🎯 Future Enhancements

### Potential Additions (when needed)
1. **Message Queue**: RabbitMQ for async processing
2. **File Service**: Image upload and processing
3. **Notification Service**: Email and push notifications
4. **Analytics Service**: User behavior tracking
5. **Search Service**: Elasticsearch for advanced search

### Scaling Strategy
1. **Horizontal Scaling**: Deploy multiple instances
2. **Database Sharding**: When user base grows
3. **CDN Integration**: For static content
4. **Caching Layer**: Redis cluster for performance
5. **Microservices**: Re-introduce when complexity justifies it

---

## 📞 Support

- **Local Development**: See `DEPLOYMENT_GUIDE.md`
- **Railway Deployment**: Follow Railway section in deployment guide
- **Issues**: Check logs with `railway logs`
- **Performance**: Monitor with `/actuator/metrics` 