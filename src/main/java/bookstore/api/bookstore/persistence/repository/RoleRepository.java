package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Apr-21
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRoleName(String roleName);

    List<RoleEntity> findAll();
}
