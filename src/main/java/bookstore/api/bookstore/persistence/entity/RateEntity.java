package bookstore.api.bookstore.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Entity
@Table(name = "rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private BookEntity book;
    @Max(value = 10)
    @Positive
    private Integer rate;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserEntity user;

    public RateEntity(BookEntity book, Integer rate, UserEntity user) {
        this.book = book;
        this.rate = rate;
        this.user = user;
    }

}
