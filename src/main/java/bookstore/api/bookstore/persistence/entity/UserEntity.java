package bookstore.api.bookstore.persistence.entity;

import bookstore.api.bookstore.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, length = 30)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_favorite_books",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"))
    private List<BookEntity> favoriteBooks;

    public void addFavoriteBook(BookEntity book) {
        if (favoriteBooks == null) {
            favoriteBooks = new ArrayList<>();
        }
        this.favoriteBooks.add(book);
    }

    public void removeFavoriteBook(BookEntity book) {
        if (favoriteBooks != null) {
            this.favoriteBooks.remove(book);
        }
    }

}
