package prasetyo.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prasetyo.jpa.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  
}
