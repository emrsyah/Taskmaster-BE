package prasetyo.jpa.middleware;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.UserRepository;
import prasetyo.jpa.helper.JwtHelper;
import prasetyo.jpa.providers.UnauthorizedException;

@Slf4j
@Component("auth")
public class AuthMiddleware implements Middleware {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception {
        log.info("Masuk middleware auth");
        String authHeader = request.getHeader("Authorization");
        log.info("Auth header: {}", authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid auth header format or missing auth header");
            throw new UnauthorizedException("Unauthorized");
        }
        
        String token = authHeader.substring(7);
        log.info("Extracted token: {}", token);
        
        String email;
        try {
            email = jwtHelper.getSubjectFromToken(token);
            log.info("Extracted email from token: {}", email);
        } catch (Exception e) {
            log.error("Failed to validate token", e);
            throw new UnauthorizedException("Invalid token");
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        log.info("Found user: {}", userOpt.isPresent());
        
        if (userOpt.isEmpty()) {
            log.warn("No user found for email: {}", email);
            throw new UnauthorizedException("Unauthorized");
        }
        
        log.info("Setting current user with id: {}", userOpt.get().getId());
        request.setAttribute("currentUser", userOpt.get());
    }
}