package prasetyo.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import prasetyo.jpa.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  public Optional<Category> findByToken(String token);
  public Optional<Category> findByIdCategory(String idCategory);
}
