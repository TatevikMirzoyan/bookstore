package bookstore.api.bookstore.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @ElementCollection
    @CollectionTable(name = "book_genre", joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"))
    private List<String> genres;
    @Column(nullable = false)
    private Double price = 0.0;
    @Column(nullable = false, unique = true)
    private String isbn;
    @Column(nullable = false)
    private Integer publishedYear;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PublisherEntity publisher;
    private Double averageRate = 0.0;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "book_image",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private List<FileEntity> images;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
    private List<AuthorEntity> authors;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RateEntity> rates;

    @Transient
    private String imageURL;


    public Double composeAverageRate(List<RateEntity> rates) {
        if (rates != null) {
            if (rates.size() != 0) {
                DecimalFormat df = new DecimalFormat("#.##");
                return Double.valueOf(df.format((this.rates.stream()
                        .mapToDouble(RateEntity::getRate)
                        .sum() / rates.size())));
            }
        }
        return 0.0;
    }


}
