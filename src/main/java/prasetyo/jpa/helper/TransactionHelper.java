package prasetyo.jpa.helper;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Component
public class TransactionHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionHelper.class);

    @Autowired
    private DatabaseHealthHelper databaseHealthHelper;

    /**
     * Executes a transactional operation with proper rollback handling.
     * If any database exception occurs, the transaction will be marked for rollback.
     */
    public <T> T executeWithRollback(Supplier<T> operation, String operationName) {
        try {
            return operation.get();
        } catch (DataAccessException e) {
            logger.error("Database error in operation '{}': {}", operationName, e.getMessage(), e);
            databaseHealthHelper.handleTransactionError(e);
            markTransactionForRollback();
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in operation '{}': {}", operationName, e.getMessage(), e);
            databaseHealthHelper.handleTransactionError(e);
            markTransactionForRollback();
            throw e;
        }
    }

    /**
     * Executes a transactional operation without return value with proper rollback handling.
     */
    public void executeVoidWithRollback(Runnable operation, String operationName) {
        try {
            operation.run();
        } catch (DataAccessException e) {
            logger.error("Database error in operation '{}': {}", operationName, e.getMessage(), e);
            databaseHealthHelper.handleTransactionError(e);
            markTransactionForRollback();
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in operation '{}': {}", operationName, e.getMessage(), e);
            databaseHealthHelper.handleTransactionError(e);
            markTransactionForRollback();
            throw e;
        }
    }

    /**
     * Marks the current transaction for rollback only.
     * This prevents the transaction from being committed even if no exception is thrown.
     */
    private void markTransactionForRollback() {
        try {
            if (TransactionAspectSupport.currentTransactionStatus().isNewTransaction() || 
                !TransactionAspectSupport.currentTransactionStatus().isRollbackOnly()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.info("Transaction marked for rollback");
            }
        } catch (Exception e) {
            logger.warn("Failed to mark transaction for rollback: {}", e.getMessage());
        }
    }

    /**
     * Checks if the current transaction is marked for rollback.
     */
    public boolean isTransactionRollbackOnly() {
        try {
            return TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
        } catch (Exception e) {
            logger.warn("Failed to check transaction rollback status: {}", e.getMessage());
            return false;
        }
    }
} 