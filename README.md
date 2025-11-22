# ShareIt 🎯

**ShareIt** is a sharing service platform that allows users to rent out their items to others and book items from other users. It's a microservices-based application built with Spring Boot.

## 🚀 Features

- **User Management** - Create, update, and manage user profiles
- **Item Management** - Add, edit, and search for available items
- **Booking System** - Book items with various status states (WAITING, APPROVED, REJECTED, CANCELED)
- **Item Requests** - Create and manage requests for items
- **Comments & Reviews** - Add comments to items after booking
- **Search Functionality** - Search available items by name or description
- **Pagination** - Efficient data retrieval with pagination support

## 🏗 Architecture

The project follows a microservices architecture with two main modules:

### Gateway Module (`gateway`)
- API Gateway handling incoming requests
- Request validation and error handling
- Client communication with the main server
- REST endpoint exposure

### Server Module (`server`)
- Core business logic implementation
- Data persistence with JPA/Hibernate
- Database operations and business rules
- Internal API endpoints

## 🛠 Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.3.2**
- **Spring MVC** - REST API development
- **Spring Data JPA** - Database operations
- **Hibernate** - ORM implementation
- **PostgreSQL** - Primary database
- **H2 Database** - Testing database
- **Lombok** - Reduced boilerplate code
- **Maven** - Dependency management

### Containerization
- **Docker** - Application containerization
- **Docker Compose** - Multi-container orchestration

### Testing
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration testing
- **Testcontainers** - Database testing

### Code Quality
- **Checkstyle** - Code style enforcement
- **JaCoCo** - Code coverage reporting

## 📁 Project Structure
shareit/  
├── gateway/              # API Gateway module  
│   ├── src/main/java/ru/practicum/shareit/  
│   │   ├── booking/     # Booking controllers and clients  
│   │   ├── item/        # Item controllers and clients  
│   │   ├── request/     # Item request controllers  
│   │   ├── user/        # User controllers and clients  
│   │   └── client/      # Base HTTP client  
│   └── src/test/java/   # Gateway tests  
├── server/              # Main business logic server  
│   ├── src/main/java/ru/practicum/shareit/  
│   │   ├── booking/    # Booking service and repository  
│   │   ├── item/       # Item service and repository  
│   │   ├── request/    # Item request service  
│   │   ├── user/       # User service and repository  
│   │   └── exception/  # Custom exception handling  
│   └── src/test/java/  # Comprehensive test suite  
├── docker-compose.yml   # Docker configuration  
└── pom.xml             # Maven configuration  

## 📚 API Documentation

### Users (`/users`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users` | Create a new user |
| GET | `/users/{id}` | Get user by ID |
| GET | `/users` | Get all users |
| PATCH | `/users/{id}` | Update user information |
| DELETE | `/users/{id}` | Delete user |

### Items (`/items`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/items` | Create a new item |
| GET | `/items/{id}` | Get item by ID |
| GET | `/items` | Get owner's items |
| PATCH | `/items/{id}` | Update item information |
| GET | `/items/search` | Search available items |
| POST | `/items/{itemId}/comment` | Add comment to item |

### Bookings (`/bookings`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/bookings` | Create a new booking |
| PATCH | `/bookings/{bookingId}` | Approve/reject booking |
| PATCH | `/bookings/{bookingId}/cancel` | Cancel booking |
| GET | `/bookings/{bookingId}` | Get booking by ID |
| GET | `/bookings` | Get user's bookings |
| GET | `/bookings/owner` | Get owner's bookings |

### Item Requests (`/requests`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/requests` | Create a new item request |
| GET | `/requests` | Get user's requests |
| GET | `/requests/all` | Get all requests (paginated) |
| GET | `/requests/{requestId}` | Get request by ID |