package prasetyo.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.RegularTask;

@Repository
public interface RegularTaskRepository extends JpaRepository<RegularTask, Long> {
  public Optional<List<RegularTask>> findByToken(String token);
  public Optional<RegularTask> findByIdTask(Long idTask);
}
