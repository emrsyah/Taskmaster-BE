package prasetyo.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.RegularTask;
import prasetyo.jpa.entity.User;

@Repository
public interface RegularTaskRepository extends JpaRepository<RegularTask, String> {
    List<RegularTask> findByUser(User user);
    Optional<RegularTask> findByUuid(String uuid);
}
