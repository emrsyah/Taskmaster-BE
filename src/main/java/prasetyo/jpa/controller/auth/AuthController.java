package prasetyo.jpa.controller.auth;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.helper.JwtHelper;
import prasetyo.jpa.helper.ResponseHelper;
import prasetyo.jpa.middleware.UseMiddleware;
import prasetyo.jpa.repository.UserRepository;
import prasetyo.jpa.request.auth.LoginRequest;
import prasetyo.jpa.request.auth.RegisterRequest;
import prasetyo.jpa.service.user.AddUserService;
import prasetyo.jpa.service.user.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AddUserService addUserService;

  @Autowired
  private UserService userService;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private JwtHelper jwtHelper;

  @Autowired
  private ResponseHelper responseHelper;

  @Autowired
  private Validator validator;

  @Autowired
  private HttpServletRequest httpServletRequest;

  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
    try {
      // Check if user already exists
      if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        return responseHelper.error("Email already exists", HttpStatus.BAD_REQUEST);
      }

      User user = new User();
      user.setUsername(request.getName());
      user.setEmail(request.getEmail());
      user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

      addUserService.handle(user);

      return responseHelper.success("User registered successfully");
    } catch (Exception e) {
      log.error("Registration error: {}", e.getMessage());
      return responseHelper.error("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
    try {
      User user = userRepository.findByEmail(request.getEmail()).orElse(null);
      if (user == null) {
        return responseHelper.error("Invalid credentials", HttpStatus.UNAUTHORIZED);
      }

      if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
        return responseHelper.error("Invalid credentials", HttpStatus.UNAUTHORIZED);
      }

      String token = jwtHelper.generateToken(user.getEmail());

      Map<String, Object> data = new HashMap<>();
      data.put("token", token);
      data.put("user", Map.of(
          "id", user.getId(),
          "username", user.getUsername(),
          "email", user.getEmail()
      ));

      return responseHelper.success("Login successful", data);
    } catch (Exception e) {
      log.error("Login error: {}", e.getMessage());
      return responseHelper.error("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/me")
  @UseMiddleware(names = { "auth" })
  public ResponseEntity<Map<String, Object>> getCurrentUser() {
    try {
      User user = (User) httpServletRequest.getAttribute("currentUser");
      if (user == null) {
        return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
      }

      // Use service method to get user data with proper transaction management
      Map<String, Object> userData = userService.getUserData(user.getId());
      return responseHelper.success("User data retrieved successfully", userData);
    } catch (Exception e) {
      log.error("Error fetching user data: {}", e.getMessage());
      return responseHelper.error("Error fetching user data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/test")
  @UseMiddleware(names = { "auth", "roles:user" })
  public ResponseEntity<Map<String, Object>> test() {
    log.info("in controlelr");
    return responseHelper.success("success try feature");
  }
}
