package prasetyo.jpa.middleware;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import prasetyo.jpa.entity.User;

@Component("roles")
public class RolesMiddleware implements Middleware {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String[] allowedRoles = param.split(",");
        String userRole = user.getRole().getName();
        boolean match = false;
        for (String role : allowedRoles) {
            if (userRole.equalsIgnoreCase(role.trim())) {
                match = true;
                break;
            }
        }

        if (!match) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not your role");
        }
    }
}