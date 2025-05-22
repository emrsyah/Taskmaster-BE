package prasetyo.jpa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import prasetyo.jpa.service.task.TaskSequenceService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TaskSequenceConfig {
    
    @Autowired
    private TaskSequenceService taskSequenceService;
    
    @EventListener(ContextRefreshedEvent.class)
    public void initializeTaskSequence() {
        log.info("Initializing task sequence on application startup");
        taskSequenceService.initializeSequence();
    }
} 