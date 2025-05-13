package prasetyo.jpa.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Middleware {
    void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception;
}