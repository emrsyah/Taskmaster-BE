package prasetyo.jpa.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskSequenceService {
    
    @Autowired
    private EntityManager entityManager;
    
    private static final String CREATE_SEQUENCE_SQL = 
        "DO $$ BEGIN CREATE SEQUENCE IF NOT EXISTS task_sequence START WITH 1 INCREMENT BY 1; END $$;";
    
    private static final String GET_NEXT_VALUE_SQL = 
        "SELECT nextval('task_sequence')";
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void initializeSequence() {
        log.info("Initializing task sequence");
        try {
            // Using DO block to handle the IF NOT EXISTS condition in PL/pgSQL
            entityManager.createNativeQuery(CREATE_SEQUENCE_SQL)
                .executeUpdate();
        } catch (Exception e) {
            log.warn("Could not create sequence (it may already exist): {}", e.getMessage());
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public Long getNextSequenceNumber() {
        try {
            // Using a simple query without prepared statement
            Query query = entityManager.createNativeQuery("SELECT nextval('task_sequence')");
            Number result = (Number) query.getSingleResult();
            return result.longValue();
        } catch (Exception e) {
            log.error("Error getting next sequence value: {}", e.getMessage());
            throw e;
        }
    }
} 