package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 24-Mar-21
 */
@Repository
public interface PublisherRepository extends JpaRepository<PublisherEntity, Long> {

    @Query(value = "select p from PublisherEntity p where p.name = :name")
    Optional<PublisherEntity> findByName(String name);

    @Query(value = "select p from PublisherEntity p")
    List<PublisherEntity> findAllPublishers();
}
