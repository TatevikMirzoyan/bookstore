package bookstore.api.bookstore.persistence.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Entity
@Table(name = "rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private BookEntity book;
    @Max(value = 10)
    @PositiveOrZero
    private Integer rate;
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    public RateEntity(BookEntity book, Integer rate, UserEntity user) {
        this.book = book;
        this.rate = rate;
        this.user = user;
    }

    @Override
    public String toString() {
        return "RateEntity{" +
                "id=" + id +
                ", book=" + book +
                ", rate=" + rate +
                ", user=" + user +
                '}';
    }
}
