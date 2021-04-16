package bookstore.api.bookstore.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String genre = "fiction";
    @Column(nullable = false)
    @Positive
    private Double price = 0.0;
    @Column(nullable = false, unique = true)
    private String isbn;
    @Column(nullable = false)
    private Integer publishedYear;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PublisherEntity publisher;
    @Column(nullable = false)
    @Positive
    private Double averageRate = 0.0;
    @ElementCollection
    @CollectionTable(name = "book_images",joinColumns =@JoinColumn(name = "book_id", referencedColumnName = "id"))
    private List<Long> imageId;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
    private List<AuthorEntity> authors;


}
