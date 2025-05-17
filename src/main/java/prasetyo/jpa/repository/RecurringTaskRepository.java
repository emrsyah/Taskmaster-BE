package prasetyo.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.RecurringTask;

@Repository
public interface RecurringTaskRepository extends JpaRepository<RecurringTask, Long> {
  public Optional<List<RecurringTask>> findByToken(String token);
  public Optional<RecurringTask> findByIdTask(String idTask);
}
