package prasetyo.jpa.middleware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.UserRepository;
import prasetyo.jpa.helper.JwtHelper;
import prasetyo.jpa.providers.UnauthorizedException;

import java.util.Optional;

@Slf4j
@Component("auth")
public class AuthMiddleware implements Middleware {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    @Transactional(readOnly = true)
    public void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception {
        try {
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
        } catch (UnauthorizedException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in auth middleware: {}", e.getMessage(), e);
            throw new UnauthorizedException("Authentication error");
        }
    }
}