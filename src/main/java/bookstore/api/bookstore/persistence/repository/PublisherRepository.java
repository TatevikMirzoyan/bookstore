package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 24-Mar-21
 */
@Repository
public interface PublisherRepository extends JpaRepository<PublisherEntity, Long> {

    Optional<PublisherEntity> findByName(String name);
}
