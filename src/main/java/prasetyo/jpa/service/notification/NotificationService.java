package prasetyo.jpa.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import prasetyo.jpa.entity.Notification;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.NotificationRepository;
import prasetyo.jpa.providers.UnauthorizedException;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUser_IdOrderByDateDesc(user.getId());
    }

    @Transactional
    public Notification markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // Check if the notification belongs to the user
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Not authorized to access this notification");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }
} 