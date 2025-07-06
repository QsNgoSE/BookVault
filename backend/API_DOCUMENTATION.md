# BookVault Backend API Documentation

## Overview
BookVault is a microservices-based e-commerce platform for book sales. This document provides comprehensive API documentation for all backend services.

## Architecture
- **Auth Service** (Port 8082): User authentication and authorization
- **Book Service** (Port 8083): Book catalog and inventory management
- **Order Service** (Port 8084): Order processing and management
- **Discovery Service** (Port 8761): Service discovery using Eureka
- **User Service** (Port 8085): User profile management (minimal implementation)

## Base URLs
- **Production**: `https://your-domain.com`
- **Development**: `http://localhost`

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Common Response Format
All API responses follow this format:
```json
{
  "success": true,
  "data": { /* response data */ },
  "message": "Success message",
  "timestamp": "2025-07-06T09:34:49.818290019"
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error message",
  "timestamp": "2025-07-06T09:34:49.818290019"
}
```

---

## 🔐 Auth Service (Port 8082)

### Base URL: `/api/auth`

#### Public Endpoints

##### Register User
- **POST** `/register`
- **Description**: Register a new user account
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "1234567890",
  "role": "USER" // USER, SELLER, ADMIN
}
```
- **Response**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER"
  },
  "message": "User registered successfully"
}
```

##### Login
- **POST** `/login`
- **Description**: Authenticate user and return JWT token
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
- **Response**: Same as registration

##### Validate Token
- **POST** `/validate`
- **Description**: Validate JWT token and return user info
- **Headers**: `Authorization: Bearer <token>`
- **Response**:
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "isActive": true,
    "isVerified": true,
    "createdAt": "2025-07-06T09:34:49.818290019"
  }
}
```

##### Health Check
- **GET** `/health`
- **Description**: Check if auth service is running
- **Response**:
```json
{
  "success": true,
  "data": "Auth service is running"
}
```

##### Clear Login Bans
- **POST** `/clear-bans`
- **Description**: Clear all login bans and failed attempts (for development)
- **Response**:
```json
{
  "success": true,
  "message": "All bans and failed attempts have been cleared"
}
```

#### Protected Endpoints

##### Get User Profile
- **GET** `/profile/{userId}`
- **Description**: Get user profile information
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Same as validate token

##### Update User Profile
- **PUT** `/profile/{userId}`
- **Description**: Update user profile information
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "9876543210"
}
```
- **Response**: Updated user profile

### Admin Endpoints

#### Base URL: `/api/auth/admin`
**Note**: All admin endpoints require ADMIN role

##### Dashboard Statistics
- **GET** `/dashboard/stats`
- **Description**: Get admin dashboard statistics
- **Response**:
```json
{
  "success": true,
  "data": {
    "totalUsers": 150,
    "activeUsers": 140,
    "totalSellers": 25,
    "totalAdmins": 5,
    "newUsersToday": 3,
    "newUsersThisWeek": 12
  }
}
```

##### User Management
- **GET** `/users` - Get all users (paginated)
  - Query params: `page=0&size=10&sortBy=createdAt&sortDir=desc`
- **GET** `/sellers` - Get all sellers
- **GET** `/regular-users` - Get all regular users
- **GET** `/users/role/{role}` - Get users by role
- **GET** `/users/{userId}` - Get user by ID
- **GET** `/users/search` - Search users
  - Query params: `query=searchTerm&limit=20`

##### User Actions
- **PUT** `/users/{userId}/status` - Update user status
  - Body: `{"action": "activate|suspend"}`
- **PUT** `/users/{userId}/role` - Update user role
  - Body: `{"role": "USER|SELLER|ADMIN"}`
- **PUT** `/users/{userId}/verify` - Verify user email
- **PUT** `/users/{userId}/reset-password` - Reset user password
- **PUT** `/users/{userId}` - Update user details
- **DELETE** `/users/{userId}` - Delete user (soft delete)

##### Bulk Operations
- **PUT** `/users/bulk-action` - Bulk user actions
  - Body: `{"userIds": ["uuid1", "uuid2"], "action": "activate|suspend|verify|delete"}`

---

## 📚 Book Service (Port 8083)

### Base URL: `/api/books`

#### Public Endpoints

##### Get All Books
- **GET** `/`
- **Description**: Retrieve paginated list of active books
- **Query Parameters**:
  - `page=0` - Page number (0-based)
  - `size=12` - Page size
  - `sortBy=createdAt` - Sort field
  - `sortDir=desc` - Sort direction
- **Response**:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "uuid",
        "title": "The Great Gatsby",
        "author": "F. Scott Fitzgerald",
        "isbn": "9780743273565",
        "description": "A classic American novel",
        "price": 12.99,
        "publishedDate": "1925-04-10",
        "coverImageUrl": "https://example.com/cover.jpg",
        "stockQuantity": 50,
        "sellerId": "uuid",
        "isActive": true,
        "rating": 4.5,
        "reviewCount": 1250,
        "language": "English",
        "pageCount": 180,
        "publisher": "Charles Scribner's Sons",
        "categories": [
          {
            "id": "uuid",
            "name": "Fiction",
            "description": "Fictional literature and novels",
            "isActive": true
          }
        ],
        "inStock": true,
        "available": true,
        "createdAt": "2025-07-06T09:34:49.818290019",
        "updatedAt": "2025-07-06T09:34:49.818290019"
      }
    ],
    "page": 0,
    "size": 12,
    "totalElements": 100,
    "totalPages": 9,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

##### Get Book by ID
- **GET** `/{id}`
- **Description**: Retrieve book details by ID
- **Response**: Single book object

##### Get Book by ISBN
- **GET** `/isbn/{isbn}`
- **Description**: Retrieve book details by ISBN
- **Response**: Single book object

##### Search Books
- **GET** `/search`
- **Description**: Search books by title, author, or description
- **Query Parameters**:
  - `q=searchTerm` - Search query (required)
  - `page=0` - Page number
  - `size=12` - Page size
  - `sortBy=relevance` - Sort field
- **Response**: Paginated book results

##### Get Books by Category
- **GET** `/category/{categoryName}`
- **Description**: Retrieve books by category name
- **Query Parameters**: `page=0&size=12`
- **Response**: Paginated book results

##### Get Books by Author
- **GET** `/author/{author}`
- **Description**: Retrieve books by author name
- **Query Parameters**: `page=0&size=12`
- **Response**: Paginated book results

##### Special Collections
- **GET** `/featured` - Get featured books
- **GET** `/bestsellers` - Get bestselling books (top-rated)
- **GET** `/new-releases` - Get newest books
- All support pagination: `page=0&size=12`

##### Filter Books
- **GET** `/filter`
- **Description**: Filter books with multiple criteria
- **Query Parameters**:
  - `title=searchTerm` - Title filter
  - `author=searchTerm` - Author filter
  - `category=categoryName` - Category filter
  - `minPrice=10.00` - Minimum price
  - `maxPrice=50.00` - Maximum price
  - `minRating=4.0` - Minimum rating
  - `page=0&size=12` - Pagination
- **Response**: Paginated filtered results

##### Get All Categories
- **GET** `/categories`
- **Description**: Retrieve all active book categories
- **Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "name": "Fiction",
      "description": "Fictional literature and novels",
      "isActive": true
    }
  ]
}
```

#### Protected Endpoints (Authentication Required)

##### Create Book
- **POST** `/`
- **Description**: Create a new book listing
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
```json
{
  "title": "New Book Title",
  "author": "Author Name",
  "isbn": "9780123456789",
  "description": "Book description",
  "price": 19.99,
  "publishedDate": "2025-01-01",
  "coverImageUrl": "https://example.com/cover.jpg",
  "stockQuantity": 100,
  "sellerId": "uuid",
  "language": "English",
  "pageCount": 250,
  "publisher": "Publisher Name",
  "categoryNames": ["Fiction", "Mystery"]
}
```
- **Response**: Created book object

##### Update Book
- **PUT** `/{id}`
- **Description**: Update existing book details
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**: Same as create (all fields optional)
- **Response**: Updated book object

##### Manage Book Status
- **PATCH** `/{id}/stock` - Update book stock
  - Query param: `stockQuantity=50`
- **PATCH** `/{id}/activate` - Activate book listing
- **PATCH** `/{id}/deactivate` - Deactivate book listing
- **DELETE** `/{id}` - Delete book listing

##### Get Books by Seller
- **GET** `/seller/{sellerId}`
- **Description**: Retrieve books by seller ID
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Array of book objects

---

## 🛒 Order Service (Port 8084)

### Base URL: `/api/orders`

#### Customer Endpoints

##### Create Order
- **POST** `/`
- **Description**: Create a new order
- **Headers**: 
  - `Authorization: Bearer <token>`
  - `X-User-Id: {userId}`
- **Request Body**:
```json
{
  "items": [
    {
      "bookId": "uuid",
      "bookTitle": "Book Title",
      "bookAuthor": "Author Name",
      "bookIsbn": "9780123456789",
      "bookImageUrl": "https://example.com/cover.jpg",
      "quantity": 2,
      "unitPrice": 19.99,
      "discountAmount": 0.00
    }
  ],
  "shippingAddress": "123 Main St",
  "shippingCity": "New York",
  "shippingState": "NY",
  "shippingPostalCode": "10001",
  "shippingCountry": "USA",
  "paymentMethod": "CREDIT_CARD",
  "customerEmail": "customer@example.com",
  "customerPhone": "1234567890",
  "customerName": "John Doe",
  "orderNotes": "Special instructions"
}
```
- **Response**:
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "userId": "uuid",
    "orderNumber": "ORD-20250706-001",
    "status": "PENDING",
    "totalAmount": 39.98,
    "shippingCost": 5.99,
    "taxAmount": 3.20,
    "discountAmount": 0.00,
    "finalAmount": 49.17,
    "paymentMethod": "CREDIT_CARD",
    "paymentStatus": "PENDING",
    "orderItems": [
      {
        "id": "uuid",
        "bookId": "uuid",
        "bookTitle": "Book Title",
        "bookAuthor": "Author Name",
        "quantity": 2,
        "unitPrice": 19.99,
        "totalPrice": 39.98
      }
    ],
    "createdAt": "2025-07-06T09:34:49.818290019",
    "updatedAt": "2025-07-06T09:34:49.818290019"
  },
  "message": "Order created successfully"
}
```

##### Get Order by ID
- **GET** `/{orderId}`
- **Description**: Get order details by ID
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Order object

##### Get Order by Order Number
- **GET** `/number/{orderNumber}`
- **Description**: Get order details by order number
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Order object

##### Get My Orders
- **GET** `/my-orders`
- **Description**: Get current user's orders
- **Headers**: 
  - `Authorization: Bearer <token>`
  - `X-User-Id: {userId}`
- **Response**: Array of order objects

##### Get My Orders (Paginated)
- **GET** `/my-orders/paged`
- **Description**: Get current user's orders with pagination
- **Headers**: 
  - `Authorization: Bearer <token>`
  - `X-User-Id: {userId}`
- **Query Parameters**: `page=0&size=10&sortBy=createdAt&sortDir=desc`
- **Response**: Paginated order results

##### Cancel Order
- **PUT** `/{orderId}/cancel`
- **Description**: Cancel an order
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
```json
{
  "reason": "Customer requested cancellation"
}
```
- **Response**: Updated order object

#### Admin Endpoints

##### Get All Orders
- **GET** `/admin/all`
- **Description**: Get all orders (admin only)
- **Headers**: `Authorization: Bearer <token>` (ADMIN role required)
- **Query Parameters**: `page=0&size=20&sortBy=createdAt&sortDir=desc`
- **Response**: Paginated order results

##### Get Orders by Status
- **GET** `/admin/status/{status}`
- **Description**: Get orders by status
- **Headers**: `Authorization: Bearer <token>` (ADMIN role required)
- **Path Parameters**: `status` - PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
- **Response**: Array of order objects

##### Update Order Status
- **PUT** `/{orderId}/status`
- **Description**: Update order status
- **Headers**: `Authorization: Bearer <token>` (ADMIN role required)
- **Request Body**:
```json
{
  "status": "CONFIRMED"
}
```
- **Response**: Updated order object

##### Update Tracking Information
- **PUT** `/{orderId}/tracking`
- **Description**: Update order tracking information
- **Headers**: `Authorization: Bearer <token>` (ADMIN role required)
- **Request Body**:
```json
{
  "trackingNumber": "TRACK123456",
  "estimatedDeliveryDate": "2025-07-15T10:00:00"
}
```
- **Response**: Updated order object

##### Search Orders
- **GET** `/admin/search`
- **Description**: Search orders by customer
- **Headers**: `Authorization: Bearer <token>` (ADMIN role required)
- **Query Parameters**: `query=searchTerm`
- **Response**: Array of matching orders

##### Get Order Statistics
- **GET** `/admin/statistics`
- **Description**: Get order statistics
- **Headers**: `Authorization: Bearer <token>` (ADMIN role required)
- **Query Parameters**: 
  - `startDate=2025-01-01T00:00:00` (optional)
  - `endDate=2025-12-31T23:59:59` (optional)
- **Response**:
```json
{
  "success": true,
  "data": {
    "totalOrders": 1250,
    "totalRevenue": 45750.50,
    "averageOrderValue": 36.60,
    "periodStart": "2025-01-01T00:00:00",
    "periodEnd": "2025-12-31T23:59:59"
  }
}
```

##### Get Orders Ready for Shipping
- **GET** `/admin/ready-for-shipping`
- **Description**: Get orders ready for shipping
- **Headers**: `Authorization: Bearer <token>` (ADMIN role required)
- **Response**: Array of orders with CONFIRMED status and COMPLETED payment

---

## 🔍 Discovery Service (Port 8761)

### Eureka Dashboard
- **URL**: `http://localhost:8761/`
- **Description**: Eureka service registry dashboard
- **Authentication**: Basic auth (admin/admin123)

### Health Check
- **GET** `/actuator/health`
- **Description**: Discovery service health check
- **Response**:
```json
{
  "status": "UP",
  "components": {
    "eureka": {
      "status": "UP",
      "details": {
        "applications": {
          "AUTH-SERVICE": 1,
          "BOOK-SERVICE": 1,
          "ORDER-SERVICE": 1,
          "USER-SERVICE": 1
        }
      }
    }
  }
}
```

---

## 👤 User Service (Port 8085)

### Status
Currently minimal implementation. Health check available:
- **GET** `/actuator/health`

---

## Data Models

### User Roles
- `USER` - Regular customer
- `SELLER` - Book seller
- `ADMIN` - System administrator

### Order Status
- `PENDING` - Order placed, awaiting confirmation
- `CONFIRMED` - Order confirmed, being processed
- `PROCESSING` - Order being prepared
- `SHIPPED` - Order shipped
- `OUT_FOR_DELIVERY` - Order out for delivery
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled
- `REFUNDED` - Order refunded

### Payment Methods
- `CREDIT_CARD` - Credit card payment
- `DEBIT_CARD` - Debit card payment
- `PAYPAL` - PayPal payment
- `BANK_TRANSFER` - Bank transfer
- `CASH_ON_DELIVERY` - Cash on delivery

### Payment Status
- `PENDING` - Payment pending
- `PROCESSING` - Payment processing
- `COMPLETED` - Payment completed
- `FAILED` - Payment failed
- `REFUNDED` - Payment refunded

---

## Error Codes

### HTTP Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

### Common Error Messages
- `"Email already registered"` - User registration with existing email
- `"Invalid email or password"` - Login with wrong credentials
- `"User not found"` - User ID not found
- `"Book not found"` - Book ID not found
- `"Order not found"` - Order ID not found
- `"Insufficient stock"` - Not enough books in stock
- `"Invalid authorization header"` - Missing or invalid JWT token
- `"Access denied"` - Insufficient permissions

---

## Rate Limiting

### Login Attempts
- Maximum 5 failed attempts per user account
- Account locked for 15 minutes after 3 failed attempts
- Account permanently locked after 5 failed attempts
- IP address blocked for 30 minutes after 5 failed attempts

### API Rate Limits
- 100 requests per minute per IP for public endpoints
- 1000 requests per minute per authenticated user
- 5000 requests per minute for admin users

---

## Testing

### Sample Test Data
The system includes sample data for testing:
- 10 books across various categories
- 10 book categories
- Test user accounts with different roles

### Postman Collection
Import the Postman collection for easy API testing:
```bash
# Collection available at: backend/postman/BookVault-API.postman_collection.json
```

### cURL Examples

#### Register User
```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "1234567890",
    "role": "USER"
  }'
```

#### Login User
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "password123"
  }'
```

#### Get Books
```bash
curl -X GET "http://localhost:8083/api/books?page=0&size=5"
```

#### Search Books
```bash
curl -X GET "http://localhost:8083/api/books/search?q=gatsby&page=0&size=10"
```

#### Create Order (with auth token)
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-User-Id: YOUR_USER_ID" \
  -d '{
    "items": [
      {
        "bookId": "uuid",
        "bookTitle": "Test Book",
        "bookAuthor": "Test Author",
        "bookIsbn": "9780123456789",
        "quantity": 1,
        "unitPrice": 19.99,
        "discountAmount": 0.00
      }
    ],
    "shippingAddress": "123 Test St",
    "shippingCity": "Test City",
    "shippingState": "TS",
    "shippingPostalCode": "12345",
    "shippingCountry": "USA",
    "paymentMethod": "CREDIT_CARD",
    "customerEmail": "test@example.com",
    "customerPhone": "1234567890",
    "customerName": "Test User"
  }'
```

---

## Monitoring & Logging

### Health Checks
All services provide health check endpoints:
- Auth Service: `GET /actuator/health`
- Book Service: `GET /actuator/health`
- Order Service: `GET /actuator/health`
- Discovery Service: `GET /actuator/health`
- User Service: `GET /actuator/health`

### Metrics
Actuator metrics available at `/actuator/metrics` for all services.

### Logging
All services log to stdout with structured logging format.

---

## Security

### JWT Token
- Algorithm: HS256
- Expiration: 24 hours
- Contains: user ID, email, role, issue time

### CORS
All services configured to allow cross-origin requests for development.

### SQL Injection Prevention
All database queries use parameterized statements.

### Input Validation
All endpoints validate input using Bean Validation annotations.

---

## Support

For technical support or questions about the API, please contact:
- Email: support@bookvault.com
- Documentation: https://api.bookvault.com/docs
- Status Page: https://status.bookvault.com

---

**Last Updated**: 2025-07-06
**API Version**: 1.0.0 