package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.BookEntity;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByIsbn(String isbn);

    @Query(value = "select distinct b from BookEntity b " +
            " inner join b.authors a " +
            " inner join b.publisher p WHERE " +
            " (:title is null or b.title like concat('%', :title, '%')) and " +
            " (:isbn is null or b.isbn like concat('%', :isbn, '%')) and " +
            " (:genre is null or :genre member of b.genres) and " +
            " (:author is null or a.name like concat('%', :author, '%')) and " +
            " (:publisher is null or p.name like concat('%', :publisher, '%')) and " +
            " (:publishedYear is null or b.publishedYear = :publishedYear) and " +
            " (:minPrice is null or b.price >= :minPrice) and " +
            " (:minRate is null or b.averageRate >= :minRate) ")
    Page<BookEntity> findAllWithPagination(@Param("title") String title,
                                           @Param("isbn") String isbn,
                                           @Param("genre") String genre,
                                           @Param("author") String author,
                                           @Param("publisher") String publisher,
                                           @Param("publishedYear") Integer publishedYear,
                                           @Param("minPrice") Double minPrice,
                                           @Param("minRate") Double minRate, Pageable pageable);


    @Query(value = "select b.title from BookEntity b")
    List<String> findAllBookTitles();

    @Query(value = "select b.isbn from BookEntity b")
    List<String> findAllBookIsbn();
}
