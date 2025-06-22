# BookVault Backend - Microservices Architecture

This is the backend implementation for BookVault, built with Java 17+ and Spring Boot microservices.

## Architecture Overview

The backend follows a microservices architecture pattern with the following services:

```
BookVault Backend
├── api-gateway/           # API Gateway (Spring Cloud Gateway)
├── discovery-service/     # Service Discovery (Eureka)
├── config-service/        # Configuration Service (Spring Cloud Config)
├── auth-service/          # Authentication & Authorization
├── user-service/          # User Management
├── book-service/          # Book/Product Management
├── order-service/         # Order Management
├── notification-service/  # Notifications & Support
├── file-service/          # File Upload/Management
└── shared/               # Shared libraries and utilities
```

## Technology Stack

- **Java**: 17+
- **Spring Boot**: 3.2+
- **Spring Cloud**: 2023.0.x
- **Spring Security**: 6.x
- **Spring Data JPA**: Hibernate 6.x
- **Database**: PostgreSQL (primary), Redis (caching)
- **Message Queue**: RabbitMQ
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **Documentation**: OpenAPI 3 (Swagger)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Testcontainers

## Microservices

### 1. API Gateway (Port: 8080)
- Routes requests to appropriate services
- Authentication & Authorization
- Rate limiting and circuit breaker
- CORS configuration
- Request/Response logging

### 2. Discovery Service (Port: 8761)
- Eureka server for service registration
- Service health monitoring
- Load balancing

### 3. Config Service (Port: 8888)
- Centralized configuration management
- Environment-specific configs
- Dynamic configuration updates

### 4. Auth Service (Port: 8081)
- JWT token generation and validation
- User authentication
- Role-based authorization
- OAuth2 integration (future)

### 5. User Service (Port: 8082)
- User registration and profile management
- User roles (USER, SELLER, ADMIN)
- Address management
- User preferences

### 6. Book Service (Port: 8083)
- Book catalog management
- Categories and search
- Book reviews and ratings
- Inventory management

### 7. Order Service (Port: 8084)
- Order processing
- Payment integration
- Order tracking
- Invoice generation

### 8. Notification Service (Port: 8085)
- Email notifications
- In-app notifications
- Support ticket management
- Newsletter management

### 9. File Service (Port: 8086)
- File upload and management
- Image processing
- CDN integration
- File security

## Database Design

### User Service Database
- users
- user_roles
- user_addresses
- user_preferences

### Book Service Database
- books
- categories
- book_categories
- book_reviews
- book_ratings
- inventory

### Order Service Database
- orders
- order_items
- payments
- order_status_history

### Auth Service Database
- auth_tokens
- refresh_tokens
- user_sessions

## Security

- **JWT Authentication**: Stateless authentication
- **Role-based Access Control**: USER, SELLER, ADMIN
- **HTTPS**: All communications encrypted
- **CORS**: Configured for frontend integration
- **Input Validation**: Request validation and sanitization
- **Rate Limiting**: API rate limiting

## API Documentation

Each service provides OpenAPI 3.0 documentation accessible at:
- `http://localhost:{port}/api/docs` (Swagger UI)
- `http://localhost:{port}/api/docs.json` (OpenAPI JSON)

## Getting Started

1. **Prerequisites**
   - Java 17+
   - Maven 3.8+
   - Docker & Docker Compose
   - PostgreSQL 15+
   - Redis 7+

2. **Setup Database**
   ```bash
   docker-compose up -d postgres redis
   ```

3. **Start Services** (in order)
   ```bash
   # Start discovery service first
   cd discovery-service && mvn spring-boot:run
   
   # Start config service
   cd config-service && mvn spring-boot:run
   
   # Start other services
   cd auth-service && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd book-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   cd file-service && mvn spring-boot:run
   
   # Start API gateway last
   cd api-gateway && mvn spring-boot:run
   ```

4. **Access Services**
   - API Gateway: http://localhost:8080
   - Eureka Dashboard: http://localhost:8761
   - Config Service: http://localhost:8888

## Development

### Running with Docker
```bash
docker-compose up -d
```

### Running Individual Services
```bash
cd {service-name}
mvn spring-boot:run
```

### Testing
```bash
# Run all tests
mvn test

# Run integration tests
mvn verify
```

## Deployment

### Production Deployment
- **Containerization**: Docker containers
- **Orchestration**: Docker Compose / Kubernetes
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **CI/CD**: GitHub Actions / Jenkins

### Environment Configuration
- **Development**: Local database, in-memory caching
- **Staging**: Shared database, Redis caching
- **Production**: Clustered database, Redis cluster, CDN

## Monitoring & Observability

- **Health Checks**: Spring Boot Actuator
- **Metrics**: Micrometer + Prometheus
- **Distributed Tracing**: Spring Cloud Sleuth + Zipkin
- **Logging**: Structured logging with Logback
- **Dashboards**: Grafana dashboards

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow coding standards
4. Write tests
5. Submit pull request

## License

MIT License - see LICENSE file for details. 