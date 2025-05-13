package prasetyo.jpa.middleware;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.UserRepository;

@Slf4j
@Component("auth")
public class AuthMiddleware implements Middleware {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception {
        log.info("Masuk middleware auth");
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            // throw new Exception("Unauthorized");
            return;
        }
        String token = authHeader.substring(7);
        Optional<User> userOpt = userRepository.findByToken(token);
        if (userOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            // throw new Exception("Unauthorized");
            return;
        }
        request.setAttribute("currentUser", userOpt.get());
    }
}