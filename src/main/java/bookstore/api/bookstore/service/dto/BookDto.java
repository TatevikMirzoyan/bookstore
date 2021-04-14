package bookstore.api.bookstore.service.dto;

import bookstore.api.bookstore.persistence.entity.AuthorEntity;
import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 14-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    @JsonIgnore
    private Long id;
    @NotBlank(message = "Book title must not be empty")
    private String title;
    private String genre;
    @NotNull(message = "Book price must not be empty")
    @PositiveOrZero
    private Double price;
    @NotBlank(message = "Book ISBN must not be empty")
    private String isbn;
    @Positive
    private Integer publishedYear;
    private PublisherEntity publisher;
    @PositiveOrZero
    @Max(value = 10, message = "Book average rate can be maximum 10")
    private Double averageRate;
    private List<Long> images;
    private List<AuthorEntity> authors;

}
