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

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private Validator validator;

  @Autowired
  private ResponseHelper responseHelper;

  @Autowired
  private JwtHelper jwtHelper;

  @Autowired
  private AddUserService addUserService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder bcrCryptPasswordEncoder;

  @Autowired
  private HttpServletRequest httpServletRequest;

  @PostMapping(path = "/register")
  public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) throws Exception {
    log.info("Attempting registration for email: {}", request.getEmail());
    User user = new User();
    user.setUsername(request.getEmail());
    user.setEmail(request.getEmail());
    user.setPassword(bcrCryptPasswordEncoder.encode(request.getPassword()));

    addUserService.handle(user);

    log.info("Registration successful for email: {}", request.getEmail());
    return responseHelper.success("Success registration, please login");
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) throws Exception {
    log.info("Login attempt for email: {}", request.getEmail());
    User user = userRepository.findByEmail(request.getEmail()).orElse(null);

    if (user == null || !bcrCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
      log.warn("Login failed for email: {}", request.getEmail());
      return responseHelper.error("Email atau password salah", HttpStatus.UNAUTHORIZED);
    }

    String token = jwtHelper.generateToken(user.getEmail());
    log.info("Login successful for email: {}. Generated JWT: {}", request.getEmail(), token);

    return responseHelper.success("Login berhasil", token);
  }

  @GetMapping("/me")
  @UseMiddleware(names = { "auth" })
  public ResponseEntity<Map<String, Object>> getCurrentUser() {
    User user = (User) httpServletRequest.getAttribute("currentUser");
    if (user == null) {
      return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    Map<String, Object> userData = new HashMap<>();
    userData.put("id", user.getId());
    userData.put("username", user.getUsername());
    userData.put("email", user.getEmail());
    userData.put("taskCount", user.getTaskList() != null ? user.getTaskList().size() : 0);
    userData.put("categoryCount", user.getCategoryList() != null ? user.getCategoryList().size() : 0);

    return responseHelper.success("User data retrieved successfully", userData);
  }

  @GetMapping("/test")
  @UseMiddleware(names = { "auth", "roles:user" })
  public ResponseEntity<Map<String, Object>> test() {
    log.info("in controlelr");
    return responseHelper.success("success try feature");
  }
}
