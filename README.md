# TaskMaster API

A Spring Boot-based task management API that supports both regular and recurring tasks with JWT authentication and custom middleware.

## Features

- User authentication and authorization with JWT tokens
- Task management (regular and recurring tasks)
- Task categorization with color support
- Task status tracking (pending, in progress, completed, archived)
- Recurring task scheduling with done dates tracking
- Sequence number generation for tasks
- Custom middleware system for authorization
- Advanced validation with custom annotations
- User profile management
- Category archiving/unarchiving
- Task archiving/unarchiving

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/taskmaster.git
cd taskmaster
```

2. Create PostgreSQL database:
```sql
CREATE DATABASE taskmaster;
```

3. Update `application.properties` with your database credentials:
```properties
spring.application.name=jpa
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmaster
spring.jpa.hibernate.ddl-auto=update
server.port=8000
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/me` - Get current user profile (requires auth)

### Tasks

#### Core CRUD Operations
- `POST /api/tasks` - Create a new task (regular or recurring)
- `GET /api/tasks` - Get all tasks (both regular and recurring)
- `GET /api/tasks/{uuid}` - Get a specific task by UUID
- `PUT /api/tasks/{uuid}` - Update a task
- `DELETE /api/tasks/{uuid}` - Delete a task

#### Task Type Specific (Legacy - Deprecated)
- `GET /api/tasks/regular` - Get all regular tasks
- `GET /api/tasks/recurring` - Get all recurring tasks
- `GET /api/tasks/regular/{uuid}` - Get a specific regular task
- `GET /api/tasks/recurring/{uuid}` - Get a specific recurring task
- `DELETE /api/tasks/regular/{uuid}` - Delete a regular task (deprecated)
- `DELETE /api/tasks/recurring/{uuid}` - Delete a recurring task (deprecated)

#### Task Status Management
- `PUT /api/tasks/{uuid}/start` - Mark task as in progress
- `PUT /api/tasks/{uuid}/complete` - Mark task as completed
- `PUT /api/tasks/{uuid}/undo-complete` - Undo task completion
- `PUT /api/tasks/{uuid}/archive` - Archive a task
- `PUT /api/tasks/{uuid}/unarchive` - Unarchive a task

### Categories
- `POST /api/categories` - Create a new category
- `GET /api/categories` - Get all categories (with optional `includeArchived` query parameter)
- `GET /api/categories/{id}` - Get a specific category
- `PUT /api/categories/{id}` - Update a category
- `DELETE /api/categories/{id}` - Delete a category
- `PUT /api/categories/{id}/archive` - Archive a category
- `PUT /api/categories/{id}/unarchive` - Unarchive a category

## Request/Response Examples

### Authentication

#### Register User
```json
POST /api/auth/register
{
    "email": "user@example.com",
    "name": "John Doe",
    "password": "securepassword"
}
```

#### Login User
```json
POST /api/auth/login
{
    "email": "user@example.com",
    "password": "securepassword"
}
```

#### Login Response
```json
{
    "status": true,
    "statusCode": 200,
    "message": "Login berhasil",
    "data": "eyJhbGciOiJIUzI1NiJ9...",
    "errors": null
}
```

### Task Management

#### Create Regular Task
```json
POST /api/tasks
Authorization: Bearer <your-jwt-token>
{
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the project",
    "priority": 1,
    "taskType": "REGULAR",
    "deadline": "2025-06-01T00:00:00Z",
    "categoryId": 1
}
```

#### Create Recurring Task
```json
POST /api/tasks
Authorization: Bearer <your-jwt-token>
{
    "title": "Weekly team meeting",
    "description": "Regular team sync-up",
    "priority": 2,
    "taskType": "RECURRING",
    "recurrenceDays": ["MONDAY", "WEDNESDAY", "FRIDAY"],
    "categoryId": 2
}
```

#### Update Task
```json
PUT /api/tasks/{uuid}
Authorization: Bearer <your-jwt-token>
{
    "title": "Updated task title",
    "description": "Updated description",
    "priority": 3,
    "taskType": "REGULAR",
    "isCompleted": false,
    "isArchived": false,
    "categoryId": 1,
    "deadline": "2025-07-01T00:00:00Z"
}
```

### Category Management

#### Create Category
```json
POST /api/categories
Authorization: Bearer <your-jwt-token>
{
    "name": "Work Tasks",
    "description": "Tasks related to work projects",
    "color": "#FF5733"
}
```

#### Update Category
```json
PUT /api/categories/{id}
Authorization: Bearer <your-jwt-token>
{
    "name": "Updated Category",
    "description": "Updated description",
    "color": "#33FF57",
    "isArchived": false
}
```

### Success Response
```json
{
    "status": true,
    "statusCode": 201,
    "message": "Regular task created successfully",
    "data": {
        "uuid": "123e4567-e89b-12d3-a456-426614174000",
        "sequenceNumber": 1,
        "title": "Complete project documentation",
        "description": "Write comprehensive documentation for the project",
        "isCompleted": false,
        "status": "PENDING",
        "createdAt": "2025-01-01T10:00:00Z",
        "updatedAt": "2025-01-01T10:00:00Z",
        "priority": 1,
        "deadline": "2025-06-01T00:00:00Z",
        "isArchived": false,
        "category": {
            "id": 1,
            "name": "Work Tasks",
            "description": "Tasks related to work projects",
            "color": "#FF5733",
            "isArchived": false
        }
    },
    "errors": null
}
```

## Error Handling

The API uses standard HTTP status codes and returns detailed error messages:

- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

### Validation Error Response
```json
{
    "status": false,
    "statusCode": 400,
    "message": "Failed validation",
    "data": null,
    "errors": [
        "Title is required",
        "Description must be less than 1000 characters"
    ]
}
```

### Authentication Error Response
```json
{
    "status": false,
    "statusCode": 401,
    "message": "Unauthorized",
    "data": null,
    "errors": "Unauthorized"
}
```

## Authentication & Authorization

### JWT Authentication
- All protected endpoints require a JWT token in the Authorization header
- Token format: `Authorization: Bearer <your-jwt-token>`
- Tokens expire after 30 days
- Use the `/api/auth/login` endpoint to obtain a token

### Custom Middleware System
The API uses a custom middleware system with the `@UseMiddleware` annotation:
- `@UseMiddleware(names = {"auth"})` - Requires authentication
- Middleware is implemented using Spring AOP (Aspect-Oriented Programming)
- Custom validation annotations for database operations

## Validation

### Custom Validation Annotations
- `@UniqueValue` - Ensures field values are unique in the database
- `@ExistsInDatabase` - Validates that referenced entities exist
- Standard JSR-303 validation annotations for basic validation

### Request Validation Rules
- **Task Title**: Required, max 255 characters
- **Task Description**: Required, max 1000 characters
- **Priority**: Required integer
- **Category Name**: Required, max 255 characters
- **Category Color**: Optional, max 7 characters (hex color)
- **Email**: Must be valid and unique for registration

## Database Schema

### Key Entities
- **Users**: User accounts with email-based authentication
- **Regular Tasks**: Tasks with optional deadlines
- **Recurring Tasks**: Tasks that repeat on specified days
- **Categories**: Task categorization with color coding
- **Notifications**: System notifications (future feature)

### Relationships
- Users have many tasks (regular and recurring)
- Users have many categories
- Tasks belong to categories (optional)
- Tasks have status tracking (PENDING, IN_PROGRESS, COMPLETED, ARCHIVED)

## Development

### Project Structure
```
src/main/java/prasetyo/jpa/
├── annotation/         # Custom validation annotations
├── config/            # Configuration classes (CORS, etc.)
├── controller/        # REST controllers
├── entity/           # JPA entities
├── helper/           # Utility classes (JWT, Response helpers)
├── middleware/       # Custom middleware system
├── providers/        # Global exception handlers
├── repository/       # JPA repositories
├── request/          # Request DTOs
├── service/          # Business logic services
└── validator/        # Custom validators
```

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package
```

### Running the Application
```bash
# Development mode with auto-reload
./mvnw spring-boot:run

# Production mode
java -jar target/jpa-0.0.1-SNAPSHOT.jar
```

## Dependencies

### Core Dependencies
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Security (core components)
- PostgreSQL Driver
- Lombok
- Jakarta Validation
- JWT (io.jsonwebtoken)

### Development Tools
- Spring Boot DevTools
- Spring Boot Configuration Processor
- HikariCP Connection Pool

## Security Features

- **JWT-based authentication** with configurable expiration
- **Password hashing** using BCrypt
- **Request validation** with custom annotations
- **CORS configuration** for cross-origin requests
- **Custom middleware** for fine-grained access control
- **Input sanitization** and validation

## License

This project is licensed under the MIT License - see the LICENSE file for details.
