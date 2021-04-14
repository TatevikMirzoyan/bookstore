package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    Page<AuthorEntity> findByNameContaining(String name, Pageable pageable);
}
