package prasetyo.jpa.service.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.UserRepository;
import prasetyo.jpa.repository.RegularTaskRepository;
import prasetyo.jpa.repository.RecurringTaskRepository;
import prasetyo.jpa.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegularTaskRepository regularTaskRepository;

    @Autowired
    private RecurringTaskRepository recurringTaskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getUserData(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Count tasks and categories using repository methods to avoid lazy loading
            long regularTaskCount = regularTaskRepository.countByUser(user);
            long recurringTaskCount = recurringTaskRepository.countByUser(user);
            long totalTaskCount = regularTaskCount + recurringTaskCount;
            long categoryCount = categoryRepository.countByUser(user);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("taskCount", totalTaskCount);
            userData.put("regularTaskCount", regularTaskCount);
            userData.put("recurringTaskCount", recurringTaskCount);
            userData.put("categoryCount", categoryCount);

            return userData;
        } catch (Exception e) {
            log.error("Error fetching user data for user ID {}: {}", userId, e.getMessage());
            throw e;
        }
    }
} 