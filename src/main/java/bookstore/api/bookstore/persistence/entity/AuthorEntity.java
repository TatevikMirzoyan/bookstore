package bookstore.api.bookstore.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Entity
@Table(name = "authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "authors",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookEntity> books;

    public AuthorEntity(String name) {
        this.name = name;
    }
}
