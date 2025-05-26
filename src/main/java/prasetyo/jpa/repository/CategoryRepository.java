package prasetyo.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.Category;
import prasetyo.jpa.entity.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    Optional<Category> findByIdAndUser(Long id, User user);
    List<Category> findByUserAndIsArchivedFalse(User user);
}
