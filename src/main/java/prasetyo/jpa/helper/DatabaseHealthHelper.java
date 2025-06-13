package prasetyo.jpa.helper;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

@Component
public class DatabaseHealthHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthHelper.class);

    @Autowired
    private DataSource dataSource;

    /**
     * Check if the database connection is healthy
     */
    public boolean isDatabaseHealthy() {
        try {
            // Try to get a connection and execute a simple query
            try (var connection = dataSource.getConnection()) {
                try (var statement = connection.createStatement()) {
                    statement.execute("SELECT 1");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get connection pool statistics for monitoring
     */
    public void logConnectionPoolStats() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            HikariPoolMXBean poolBean = hikariDataSource.getHikariPoolMXBean();
            
            logger.info("Connection Pool Stats - Active: {}, Idle: {}, Total: {}, Waiting: {}", 
                poolBean.getActiveConnections(),
                poolBean.getIdleConnections(),
                poolBean.getTotalConnections(),
                poolBean.getThreadsAwaitingConnection());
        }
    }

    /**
     * Force close all connections in the pool to reset the connection state
     * Use this as a last resort when transaction issues persist
     */
    public void resetConnectionPool() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            try {
                logger.warn("Resetting connection pool due to persistent transaction issues");
                hikariDataSource.getHikariPoolMXBean().softEvictConnections();
                logger.info("Connection pool reset completed");
            } catch (Exception e) {
                logger.error("Failed to reset connection pool: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Check for common PostgreSQL transaction error patterns
     */
    public boolean isTransactionError(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        
        String message = throwable.getMessage();
        if (message != null) {
            message = message.toLowerCase();
            return message.contains("current transaction is aborted") ||
                   message.contains("prepared statement") &&  message.contains("already exists") ||
                   message.contains("transaction is aborted") ||
                   message.contains("commands ignored until end of transaction block");
        }
        
        // Check nested causes
        return isTransactionError(throwable.getCause());
    }

    /**
     * Handle known transaction errors with appropriate recovery actions
     */
    public void handleTransactionError(Throwable throwable) {
        if (isTransactionError(throwable)) {
            logger.warn("Detected PostgreSQL transaction error: {}", throwable.getMessage());
            logConnectionPoolStats();
            
            // For persistent issues, consider resetting the connection pool
            // This is commented out as it's a drastic measure
            // resetConnectionPool();
        }
    }
} 