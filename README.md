# Taskmaster Backend

Taskmaster is a powerful and modern backend API for managing your daily tasks and categories, built with Java Spring Boot. It provides secure authentication, robust validation, and a clean, modular structure to help you build productivity apps or integrate task management into your own projects.

---

## 📖 What is Taskmaster?
Taskmaster is the backend service for the Taskmaster app—a productivity tool that lets users create, organize, and manage regular and recurring tasks, as well as categorize them for better workflow. The backend is designed for reliability, security, and extensibility.

---

## 🚩 Key Features
- **User Authentication & Authorization**: Secure JWT-based login and role-based access control.
- **Task Management**: Create, update, delete, and list both regular and recurring tasks.
- **Category Management**: Organize tasks into categories, with full CRUD support.
- **DTO-based Validation**: All create/update endpoints use DTOs for input validation, ensuring data integrity.
- **Custom Validation Annotations**: Easily check for unique or existing values in the database.
- **Middleware Support**: Protect routes with authentication and role checks using simple annotations.
- **Consistent API Responses**: All responses follow a clean, predictable structure for easy frontend integration.
- **Global Exception Handling**: Friendly error messages and validation feedback.
- **Ready for Testing**: JUnit 5 and Mockito recommended for unit and integration tests.

---

## 🗂️ Main Modules
- **Authentication**: Register and login endpoints, JWT token generation.
- **Tasks**: Endpoints for regular and recurring tasks, including filtering and archiving.
- **Categories**: Endpoints for creating, updating, deleting, and listing categories.

---

## 📦 Example API Endpoints

### Auth
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive a JWT token

### Tasks
- `POST /api/tasks` — Create a new task (regular or recurring)
- `GET /api/tasks` — List all tasks (both regular and recurring)
- `GET /api/tasks/{id}` — Get a specific task (either regular or recurring)
- `PUT /api/tasks/{id}` — Update a task (either regular or recurring)
- `DELETE /api/tasks/regular/{id}` — Delete a regular task
- `DELETE /api/tasks/recurring/{id}` — Delete a recurring task
- `PUT /api/tasks/{id}/start` — Mark a task as in progress
- `PUT /api/tasks/{id}/complete` — Mark a task as completed

Task Status Lifecycle:
- PENDING (Initial state)
- IN_PROGRESS (Task is being worked on)
- COMPLETED (Task is done)
- ARCHIVED (Task is archived)

### Categories
- `POST /api/categories` — Create a new category
- `GET /api/categories` — List all categories
- `PUT /api/categories/{id}` — Update a category
- `DELETE /api/categories/{id}` — Delete a category

---

## 🧪 Testing

Taskmaster is designed for easy testing. Use JUnit 5 for writing tests and Mockito for mocking dependencies.

**Example Unit Test:**
```java
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prasetyo.jpa.service.category.CategoryService;
import prasetyo.jpa.repository.CategoryRepository;
import prasetyo.jpa.entity.Category;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryService categoryService;
    @Test
    public void testCreateCategory() {
        Category category = new Category();
        category.setName("Test Category");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        Category created = categoryService.createCategory(category);
        assertNotNull(created);
        assertEquals("Test Category", created.getName());
    }
}
```

**Run all tests:**
```bash
./mvnw test
```

---

## ⚙️ Getting Started

1. **Clone the Repo**
   ```bash
   git clone https://github.com/emrsyah/Taskmaster-BE.git
   cd Taskmaster-BE
   ```
2. **Configure the Database**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. **Run the App**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or use your favorite IDE.

---

## 🛡️ Security & Middleware
- Use `@UseMiddleware(names = { "auth", "roles:user" })` to protect endpoints.
- JWT tokens are required for most task and category operations.

---

## 🧠 Custom Validation
- `@ExistsInDatabase` — Ensures a value exists in the database.
- `@UniqueValue` — Ensures a value is unique in the database.

---

## 📁 Project Structure (Key Parts)
```
src/main/java/prasetyo/jpa/
├── controller/
│   ├── auth/
│   ├── category/
│   └── task/
├── entity/
├── repository/
├── request/
│   ├── category/
│   └── task/
├── service/
│   ├── category/
│   └── task/
...
```

---

## 👨‍💻 Author

Made with ☕ & ❤️ by [@emrsyah](https://github.com/emrsyah)

---

## 📄 License

MIT License – use it, modify it, share it!
