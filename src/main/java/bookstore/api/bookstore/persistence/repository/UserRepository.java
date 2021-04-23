package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.UserEntity;
import bookstore.api.bookstore.service.criteria.UserSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = "SELECT u FROM UserEntity u inner join u.roles r WHERE " +
            " (:firstName is null or u.firstName like concat('%', :firstName, '%')) and " +
            " (:lastName is null or u.lastName like concat('%', :lastName, '%') ) and " +
            " (:username is null or u.username like concat('%', :username, '%') ) and " +
            " ((:#{#roles} is null) or (r.roleName in (:#{#roles}))) ")
    Page<UserEntity> findAllWithPagination(@Param("firstName") String firstName,
                                           @Param("lastName") String lastName,
                                           @Param("username") String username,
                                           @Param("roles") Set<String> roles,
                                           Pageable pageable);

    Optional<UserEntity> findByUsername(String username);

    @Query(value = "SELECT u.username FROM UserEntity u")
    List<String> findAllUsernames();
}
