package prasetyo.jpa.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
        "DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = 'task_sequence') THEN CREATE SEQUENCE task_sequence START WITH 1 INCREMENT BY 1; END IF; END $$;";
    
    private static final String GET_NEXT_VALUE_SQL = 
        "SELECT nextval('task_sequence')";
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void initializeSequence() {
        log.info("Initializing task sequence");
        try {
            // Execute the create sequence query with proper existence check
            entityManager.createNativeQuery(CREATE_SEQUENCE_SQL)
                .executeUpdate();
        } catch (Exception e) {
            log.warn("Could not create sequence (it may already exist): {}", e.getMessage());
            // Swallow the exception as the sequence might already exist
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long getNextSequenceNumber() {
        try {
            // Using a simple query without prepared statement
            Query query = entityManager.createNativeQuery(GET_NEXT_VALUE_SQL);
            Number result = (Number) query.getSingleResult();
            return result.longValue();
        } catch (Exception e) {
            log.error("Error getting next sequence value: {}", e.getMessage());
            throw e;
        }
    }
} 