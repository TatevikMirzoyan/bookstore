package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = "SELECT * FROM users u " +
            " WHERE (:firstName is null or u.first_name like %:firstName%) " +
            " and (:lastName is null or u.last_name like %:lastName%) " +
            " and (:role is null or u.role = :role) ", nativeQuery = true)
    Page<UserEntity> search(@Param("firstName") String firstName,
                            @Param("lastName") String lastName,
                            @Param("role") String role,
                            Pageable pageable);

    @Query("SELECT u FROM UserEntity u where u.email = :email")
    Optional<UserEntity> findByEmail(String email);

    UserEntity findByUsername(String username);
}
