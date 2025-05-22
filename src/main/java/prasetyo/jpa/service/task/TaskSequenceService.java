package prasetyo.jpa.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        "CREATE SEQUENCE IF NOT EXISTS task_sequence START WITH 1 INCREMENT BY 1";
    
    private static final String GET_NEXT_VALUE_SQL = 
        "SELECT nextval('task_sequence')";
    
    @Transactional
    public void initializeSequence() {
        log.info("Initializing task sequence");
        Query query = entityManager.createNativeQuery(CREATE_SEQUENCE_SQL);
        query.executeUpdate();
    }
    
    @Transactional
    public Long getNextSequenceNumber() {
        Query query = entityManager.createNativeQuery(GET_NEXT_VALUE_SQL);
        Number result = (Number) query.getSingleResult();
        return result.longValue();
    }
} 