# TaskMaster API

A Spring Boot-based task management API that supports both regular and recurring tasks.

## Features

- User authentication and authorization
- Task management (regular and recurring tasks)
- Task categorization
- Task status tracking (pending, in progress, completed, archived)
- Recurring task scheduling
- Sequence number generation for tasks

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
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmaster
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/refresh` - Refresh JWT token

### Tasks

#### Create & Read
- `POST /api/tasks` - Create a new task (regular or recurring)
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{uuid}` - Get a specific task
- `GET /api/tasks/regular` - Get all regular tasks
- `GET /api/tasks/recurring` - Get all recurring tasks

#### Update & Delete
- `PUT /api/tasks/{uuid}` - Update a task
- `DELETE /api/tasks/{uuid}` - Delete a task

#### Task Status Management
- `PUT /api/tasks/{uuid}/start` - Mark task as in progress
- `PUT /api/tasks/{uuid}/complete` - Mark task as completed
- `PUT /api/tasks/{uuid}/undo-complete` - Undo task completion
- `PUT /api/tasks/{uuid}/archive` - Archive a task
- `PUT /api/tasks/{uuid}/unarchive` - Unarchive a task

### Categories
- `POST /api/categories` - Create a new category
- `GET /api/categories` - Get all categories
- `PUT /api/categories/{id}` - Update a category
- `DELETE /api/categories/{id}` - Delete a category

## Request/Response Examples

### Create Regular Task
```json
POST /api/tasks
{
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the project",
    "priority": 1,
    "taskType": "REGULAR",
    "deadline": "2025-06-01T00:00:00Z"
}
```

### Create Recurring Task
```json
POST /api/tasks
{
    "title": "Weekly team meeting",
    "description": "Regular team sync-up",
    "priority": 2,
    "taskType": "RECURRING",
    "recurrenceDays": ["MONDAY", "WEDNESDAY", "FRIDAY"]
}
```

### Success Response
```json
{
    "status": true,
    "statusCode": 201,
    "message": "Task created successfully",
    "data": {
        "uuid": "123e4567-e89b-12d3-a456-426614174000",
        "title": "Complete project documentation",
        "description": "Write comprehensive documentation for the project",
        "priority": 1,
        "status": "PENDING",
        "sequenceNumber": 1,
        // ... other fields
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

Error Response Example:
```json
{
    "status": false,
    "statusCode": 400,
    "message": "Invalid task data",
    "data": null,
    "errors": "Description is required"
}
```

## Security

- JWT-based authentication
- Role-based access control
- Secure password hashing
- Request validation
- CORS configuration

## Development

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
