package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Repository
public interface RateRepository extends JpaRepository<RateEntity, Long> {

    @Query(value = "SELECT r FROM RateEntity r where r.book.id = :id")
    List<RateEntity> findAllByBookId(Long id);

    @Query(value = "SELECT r FROM RateEntity r INNER JOIN r.book b where b.id = :id")
    List<RateEntity> findAllByBookId1(Long id);

    @Query(value = "SELECT r FROM RateEntity r where r.user.id = :id")
    List<RateEntity> findAllByUserId(Long id);

}
