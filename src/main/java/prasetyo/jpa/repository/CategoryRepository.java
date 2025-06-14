package prasetyo.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.Category;
import prasetyo.jpa.entity.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.user = :user")
    List<Category> findByUser(@Param("user") User user);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.user = :user")
    Optional<Category> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT c FROM Category c WHERE c.user = :user AND c.isArchived = false")
    List<Category> findByUserAndIsArchivedFalse(@Param("user") User user);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.user = :user")
    long countByUser(@Param("user") User user);
}
