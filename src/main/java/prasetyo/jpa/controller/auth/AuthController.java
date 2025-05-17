package prasetyo.jpa.controller.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @PostMapping(path = "/register")
  public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) throws Exception {
    User user = new User();
    user.setUsername(request.getEmail());
    user.setEmail(request.getEmail());
    user.setPassword(bcrCryptPasswordEncoder.encode(request.getPassword()));

    addUserService.handle(user);

    return responseHelper.success("Success registration, please login");
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) throws Exception {
    User user = userRepository.findByEmail(request.getEmail()).orElse(null);

    if (user == null || !bcrCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
      return responseHelper.error("Email atau password salah", HttpStatus.UNAUTHORIZED);
    }

    String token = jwtHelper.generateToken(user.getEmail());

    return responseHelper.success("Login berhasil", token);
  }

  @GetMapping("/test")
  @UseMiddleware(names = { "auth", "roles:user" })
  public ResponseEntity<Map<String, Object>> test() {
    log.info("in controlelr");
    return responseHelper.success("success try feature");
  }

}
