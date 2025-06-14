package prasetyo.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.RecurringTask;
import prasetyo.jpa.entity.User;

@Repository
public interface RecurringTaskRepository extends JpaRepository<RecurringTask, String> {
    @Query("SELECT DISTINCT rt FROM RecurringTask rt WHERE rt.user = :user")
    List<RecurringTask> findByUser(@Param("user") User user);

    @Query("SELECT DISTINCT rt FROM RecurringTask rt WHERE rt.uuid = :uuid")
    Optional<RecurringTask> findByUuid(@Param("uuid") String uuid);

    @Query("SELECT COUNT(DISTINCT rt) FROM RecurringTask rt WHERE rt.user = :user")
    long countByUser(@Param("user") User user);
}
