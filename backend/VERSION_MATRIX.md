# BookVault Backend Version Compatibility Matrix

## ✅ Current Standardized Versions

### Core Platform
| Component | Version | Compatibility |
|-----------|---------|---------------|
| **Java Runtime** | **21** | ✅ All services |
| **Maven Compiler** | **3.12.1** | ✅ Java 21 compatible |
| **Spring Boot** | **3.2.4** | ✅ LTS compatible |
| **Spring Cloud** | **2023.0.1** | ✅ Spring Boot 3.2.x |

### Databases & Cache
| Component | Version | Compatibility |
|-----------|---------|---------------|
| **PostgreSQL Driver** | **42.7.3** | ✅ Java 21 + Spring Boot 3.2 |
| **Redis (Jedis)** | **5.0.2** | ✅ Spring Data Redis 3.x |
| **H2 (Testing)** | **2.2.224** | ✅ Test compatibility |

### Security & JWT
| Component | Version | Compatibility |
|-----------|---------|---------------|
| **JJWT** | **0.12.5** | ✅ Java 21 + Spring Security 6 |
| **Spring Security** | **6.2.x** | ✅ Via Spring Boot 3.2.4 |

### Documentation & Testing
| Component | Version | Compatibility |
|-----------|---------|---------------|
| **SpringDoc OpenAPI** | **2.4.0** | ✅ Spring Boot 3.2.x |
| **TestContainers** | **1.19.7** | ✅ Modern testing |
| **Mockito** | **5.11.0** | ✅ JUnit 5 + Java 21 |

### Utilities
| Component | Version | Compatibility |
|-----------|---------|---------------|
| **Lombok** | **1.18.32** | ✅ Java 21 + Latest IDEs (DISABLED) |
| **MapStruct** | **1.6.2** | ✅ Manual implementations |

## 🐳 Docker Images

### Runtime Images (Standardized to Java 21)
```dockerfile
FROM eclipse-temurin:21-jre-jammy
```

**Services Updated:**
- ✅ auth-service/Dockerfile → Java 21
- ✅ book-service/Dockerfile → Java 21  
- ✅ discovery-service/Dockerfile → Java 21
- ✅ config-service/Dockerfile → Java 21
- ✅ order-service/Dockerfile → Java 21
- ✅ user-service/Dockerfile → Java 21

## 🔧 Maven Configuration

### Parent POM Properties
```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <spring-boot.version>3.2.4</spring-boot.version>
    <spring-cloud.version>2023.0.1</spring-cloud.version>
</properties>
```

### Compiler Plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.12.1</version>
    <configuration>
        <release>${java.version}</release>
    </configuration>
</plugin>
```

## 🚀 Verification Commands

### Check Java Version Consistency
```bash
# Local Java version
java -version

# Docker Java version
docker run --rm eclipse-temurin:21-jre-jammy java -version

# Maven compilation
mvn clean compile -q
```

### Test All Services
```bash
cd backend
./test-build.sh
```

### Docker Build Test
```bash
cd backend
docker-compose build --no-cache
```

## 🔍 Troubleshooting Version Issues

### Common Version Conflicts

1. **Java Version Consistency**
   - ❌ Problem: Mixed Java versions between local environment, Dockerfile and pom.xml
   - ✅ Solution: All standardized to Java 21 LTS

2. **Spring Boot 3.2.0 vs Newer**
   - ❌ Problem: Early Spring Boot 3.2.0 had compatibility issues
   - ✅ Solution: Updated to 3.2.4 (stable)

3. **JWT 0.11.x vs 0.12.x**
   - ❌ Problem: API changes between versions
   - ✅ Solution: Updated to 0.12.5 + fixed JwtUtil

4. **Maven Compiler 3.10.1 vs 3.12.1**
   - ❌ Problem: Java 21+ compatibility issues
   - ✅ Solution: Updated to 3.12.1

5. **Lombok Annotation Processing**
   - ❌ Problem: Lombok incompatible with Java 21 compilation
   - ✅ Solution: Removed Lombok, implemented manual getters/setters/builders

### Quick Fixes

**Clean Everything:**
```bash
mvn clean
docker system prune -f
docker-compose down -v
```

**Rebuild with New Versions:**
```bash
mvn dependency:purge-local-repository
mvn clean install -DskipTests
docker-compose build --no-cache
```

## 📋 Version Update Checklist

When updating versions:

- [ ] Update parent pom.xml versions
- [ ] Verify dependency compatibility
- [ ] Update all Dockerfiles consistently  
- [ ] Test compilation: `mvn clean compile`
- [ ] Test packaging: `mvn clean package -DskipTests`
- [ ] Test Docker build: `docker-compose build`
- [ ] Run integration tests
- [ ] Update this matrix

## 🎯 Next LTS Upgrade Path

**Target: Spring Boot 3.3.x + Java 21**
- Spring Boot 3.3.x (Next LTS)
- Java 21 LTS (Eclipse Temurin)
- Keep other versions compatible
- Gradual migration plan

## 🚨 Current Known Issues

### Config Service
- **Issue**: Attempting to connect to remote Git repository over SSH
- **Status**: ❌ Failing with timeout
- **Solution**: Configure local file-based config or fix Git access

### Discovery Service  
- **Status**: ✅ Working with Java 21

### Other Services
- **Status**: ⏳ Waiting for config service dependency

---
**Last Updated:** July 2025
**Java Version:** 21 LTS  
**Spring Boot:** 3.2.4  
**Status:** 🔧 Config service needs fixing, others ready 