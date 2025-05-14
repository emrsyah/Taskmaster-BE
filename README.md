# 🚀 BoilerplateSpringboot

A ready-to-use Spring Boot boilerplate project that helps you get started **fast** with best practices like:

- ✅ JWT-based Authentication  
- 🔐 Role-based Authorization Middleware  
- 🧠 Custom Validation (Exists & Unique)  
- 🧾 Clean Response Formatting  
- 🚨 Global Exception Handling  

> Why write the same setup code again and again, when you can just fork this and start building? 😎

---

## 🔗 Repo Link

📍 GitHub: [https://github.com/PrasGi/init-project-springboot](https://github.com/PrasGi/init-project-springboot)

---

## 💡 Tech Stack

- Java 21  
- Spring Boot  
- JPA (Hibernate)  
- Lombok  
- Slf4j  
- JWT  
- Custom Middleware  
- Global Exception Handler  

---

## 📦 Features

- 🧾 **Clean Response Helper**: Consistent response format for both success and error.  
- 🔒 **JWT Authentication**: Stateless login with token generation and validation.  
- 🧠 **Custom Validation Annotations**:  
  - `@ExistsInDatabase`: Check if a value exists in DB.  
  - `@UniqueValue`: Check if a value is unique in DB.  
- 🛡️ **Middleware Support**: Use annotations to protect routes based on auth/roles.  
- ⚠️ **Global Exception Handling**: Beautiful error responses, even for validation errors.  

---

## 🚀 How to Use

### 1. Clone the Repo

```bash
git clone https://github.com/PrasGi/init-project-springboot.git
cd init-project-springboot
```

### 2. Set Up Database

Edit `application.properties` or `application.yml`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Run the Project

```bash
./mvnw spring-boot:run
```

Or use your favorite IDE (IntelliJ, Eclipse, etc).

---

## 🔐 Auth Sample: Login

### Endpoint

```
POST /login
```

### Request Body

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

### Response

```json
{
  "status": true,
  "statusCode": 200,
  "message": "Login berhasil",
  "data": "JWT_TOKEN_HERE"
}
```

---

## 🛡️ Protecting Routes with Middleware

To secure endpoints using JWT and roles, use this annotation:

```java
@UseMiddleware(names = { "auth", "roles:user" })
```

---

## 🧠 Custom Validation

### ExistsInDatabase

```java
@ExistsInDatabase(entity = User.class, field = "email", message = "Email not found")
private String email;
```

### UniqueValue

```java
@UniqueValue(entity = User.class, field = "email", message = "Email already taken")
private String email;
```

---

## ⚠️ Global Exception Handler

Handles validation errors and returns consistent error structure:

```json
{
  "status": false,
  "statusCode": 400,
  "message": "Failed validation",
  "errors": {
    "email": "Email already taken"
  }
}
```

---

## 🤝 Contributing

Feel free to fork, clone, and send PRs.  
Let’s make backend development faster and cleaner for everyone!

---

## 👨‍💻 Author

Made with ☕ & ❤️ by [@PrasGi](https://github.com/PrasGi)

---

## 📄 License

MIT License – use it, modify it, share it!
