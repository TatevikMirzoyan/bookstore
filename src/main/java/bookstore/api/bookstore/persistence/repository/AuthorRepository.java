package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    @Query(value = "select a from AuthorEntity a where a.name is null or a.name = :name")
    Page<AuthorEntity> findByNameContaining(String name, Pageable pageable);

    @Query(value = "select a from AuthorEntity a where a.name = :name")
    Optional<AuthorEntity> findByName(String name);

    @Query(value = "select a from AuthorEntity a")
    List<AuthorEntity> findAllAuthors();
}
