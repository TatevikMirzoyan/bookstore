package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByIsbn(String isbn);

    @Query(value = "select * from books b " +
            " left join book_author ba on b.id = ba.book_id " +
            " right join authors a on a.id = ba.author_id " +
            " left join publisher p on p.id = b.publisher_id " +
            " WHERE (:title is null or b.title like %:title%) " +
            " and (:isbn is null or b.isbn like %:isbn%) " +
            " and (:genre is null or b.genre like %:genre%) " +
            " and (:author is null or a.name like %:author%) " +
            " and (:publisher is null or p.name like %:publisher%) " +
            " and (:publishedYear is null or b.published_year = :publishedYear) " +
            " and (:price is null or b.price = :price) " +
            " and (:averageRate is null or b.average_rate = :averageRate) order by b.average_rate desc", nativeQuery = true)
    Page<BookEntity> search(@Param("title") String title,
                            @Param("isbn") String isbn,
                            @Param("genre") String genre,
                            @Param("author") String author,
                            @Param("publisher") String publisher,
                            @Param("publishedYear") Integer publishedYear,
                            @Param("price") BigDecimal price,
                            @Param("averageRate") BigDecimal averageRate, Pageable pageable);


    @Query(value = "SELECT b FROM Book b join book_author ba on b.id = ba.book_id  join Authors a on a.id = ba.author_id where a.name = :name", nativeQuery = true)
    Page<BookEntity> findByAuthorName(String name, Pageable pageable);

}
