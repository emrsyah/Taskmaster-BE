package prasetyo.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import prasetyo.jpa.entity.Notification;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.service.notification.NotificationService;
import prasetyo.jpa.providers.UnauthorizedException;
import prasetyo.jpa.middleware.UseMiddleware;
import prasetyo.jpa.helper.ResponseHelper;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResponseHelper responseHelper;

    @GetMapping
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getNotifications(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        
        List<Notification> notifications = notificationService.getNotificationsByUser(user);
        return responseHelper.success("Notifications retrieved successfully", notifications);
    }

    @PostMapping("/{id}/read")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable("id") Long id, HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            Notification notification = notificationService.markAsRead(id, user);
            return responseHelper.success("Notification marked as read", notification);
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
} 