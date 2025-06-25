# BookVault Backend Troubleshooting Guide

## Issues Found and Fixed

### 1. ✅ FIXED: Security Configuration Path Mismatch
**Problem**: Security config allowed `/auth/*` but controller uses `/api/auth/*`
**Fix**: Updated SecurityConfig.java to use correct paths:
```java
.requestMatchers(
    "/api/auth/register",
    "/api/auth/login", 
    "/api/auth/health",
    // ...
).permitAll()
```

### 2. ✅ FIXED: Missing UserDetailsService
**Problem**: Spring Security requires UserDetailsService for authentication
**Fix**: Created `UserDetailsServiceImpl.java` that implements UserDetailsService

### 3. ✅ FIXED: JWT Library Version Compatibility
**Problem**: JWT library methods deprecated in version 0.12.3
**Fix**: Updated JwtUtil.java to use new API:
```java
// Old (deprecated)
.setSigningKey(secretKey)
.parseClaimsJws(token)
.getBody()

// New (0.12.3+)
.verifyWith(secretKey)
.parseSignedClaims(token)
.getPayload()
```

### 4. ✅ FIXED: JWT Property Configuration
**Problem**: JWT properties mismatch between code and application.yml
**Fix**: Updated property names to match application.yml:
```java
@Value("${jwt.secret:mySecretKey}") // was bookvault.jwt.secret
@Value("${jwt.expiration:86400000}") // was bookvault.jwt.expiration
```

### 5. ✅ ADDED: Redis Configuration for Login Attempts
**Features Added**:
- Redis dependency in pom.xml
- Redis configuration in application.yml
- LoginAttemptService for ban logic
- ClientIpUtil for IP extraction
- RedisConfig for Redis template

### 6. ✅ FIXED: Complete Version Compatibility Matrix
**Problem**: Multiple version mismatches across Java, Spring Boot, dependencies, and Docker images
**Root Cause**: Inconsistent versions between Maven, Docker, and dependency management

**Comprehensive Fix Applied**:
```xml
<!-- Standardized Versions -->
<java.version>17</java.version>
<spring-boot.version>3.2.4</spring-boot.version>
<spring-cloud.version>2023.0.1</spring-cloud.version>
<jjwt.version>0.12.5</jjwt.version>
<postgresql.version>42.7.3</postgresql.version>
<redis.version>5.0.2</redis.version>
<lombok.version>1.18.32</lombok.version>
<maven-compiler-plugin.version>3.12.1</maven-compiler-plugin.version>
```

**Docker Images Updated**:
```dockerfile
# All services now use:
FROM eclipse-temurin:17-jre-jammy
```

**Services Fixed**:
- ✅ auth-service/Dockerfile → Java 17
- ✅ book-service/Dockerfile → Java 17  
- ✅ discovery-service/Dockerfile → Java 17
- ✅ config-service/Dockerfile → Java 17
- ✅ order-service/Dockerfile → Java 17
- ✅ user-service/Dockerfile → Java 17

**New Tools Added**:
- `VERSION_MATRIX.md` - Complete version compatibility guide
- `verify-versions.sh` - Automated version consistency checker

## Login Attempt Security Features

### User Banning Logic
- **3 failed attempts** → **15-minute user ban**
- **5 failed attempts from same IP** → **30-minute IP ban**
- **Automatic cleanup** on successful login
- **Redis storage** with TTL (time-to-live)

### Implementation Details
```java
// User ban after 3 failed attempts
if (loginAttemptService.isUserBanned(email)) {
    throw new BadRequestException("User banned for " + remainingTime + " minutes");
}

// IP ban after 5 failed attempts  
if (loginAttemptService.isIpBanned(clientIp)) {
    throw new BadRequestException("IP banned for " + remainingTime + " minutes");
}
```

## Testing the Build

### Quick Test
```bash
cd backend
chmod +x test-build.sh
./test-build.sh
```

### Manual Test
```bash
cd backend/auth-service
mvn clean compile
mvn package -DskipTests=true
```

## Running the Services

### Docker Compose
```bash
cd backend
docker-compose up --build
```

### Individual Service
```bash
cd backend/auth-service
mvn spring-boot:run
```

## Common Issues and Solutions

### Issue: Port Already in Use
```bash
# Check what's using port 8082
lsof -i :8082
# Kill the process
kill -9 <PID>
```

### Issue: Database Connection
- Ensure PostgreSQL is running on port 5432
- Check database credentials in application.yml
- Verify database `bookvault_auth` exists

### Issue: Redis Connection
- Ensure Redis is running on port 6379
- Check Redis configuration in application.yml
- Test with: `redis-cli ping`

### Issue: Service Discovery
- Ensure Eureka server is running on port 8761
- Check eureka client configuration
- Wait for services to register (30-60 seconds)

## Verification Steps

1. **Compilation**: `mvn compile` should succeed
2. **Packaging**: `mvn package` should create JAR file
3. **Service Health**: `curl http://localhost:8082/api/auth/health`
4. **Registration**: `curl -X POST http://localhost:8082/api/auth/register -H "Content-Type: application/json" -d '{"email":"test@example.com","password":"password123","firstName":"Test","lastName":"User"}'`
5. **Login**: `curl -X POST http://localhost:8082/api/auth/login -H "Content-Type: application/json" -d '{"email":"test@example.com","password":"password123"}'`

## Next Steps

1. Test the build with `./test-build.sh`
2. Run `docker-compose up --build`
3. Check service logs for any remaining issues
4. Test the login attempt security features

If issues persist, check the service logs:
```bash
docker-compose logs auth-service
``` 