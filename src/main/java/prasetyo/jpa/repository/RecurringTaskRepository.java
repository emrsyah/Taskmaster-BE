package prasetyo.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.RecurringTask;
import prasetyo.jpa.entity.User;

@Repository
public interface RecurringTaskRepository extends JpaRepository<RecurringTask, String> {
    List<RecurringTask> findByUser(User user);
    Optional<RecurringTask> findByUuid(String uuid);
}
