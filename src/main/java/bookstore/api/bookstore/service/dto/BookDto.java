package bookstore.api.bookstore.service.dto;

import bookstore.api.bookstore.persistence.entity.AuthorEntity;
import bookstore.api.bookstore.persistence.entity.FileEntity;
import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import bookstore.api.bookstore.persistence.entity.RateEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 14-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;
    @NotBlank(message = "Book title must not be empty")
    private String title;
    @PositiveOrZero(message = "Price  must be positive or zero")
    private Double price;
    @NotBlank(message = "Book ISBN must not be empty")
    private String isbn;
    @Positive(message = "Publication year must be positive")
    private Integer publishedYear;
    private PublisherEntity publisher;
    @PositiveOrZero
    @Max(value = 10, message = "Book average rate can be maximum 10")
    private Double averageRate;
    private List<String> genres;
    private List<FileEntity> images;
    private List<AuthorEntity> authors;
    @JsonIgnore
    private List<RateEntity> rates;

    public void addImage(FileEntity file) {
        if (images == null) {
            images = new ArrayList<>();
        }
        this.images.add(file);
    }

    public void setIsbn(String isbn) {
        isbn = isbn.replaceAll("-", "");
        this.isbn = ((isbn.length() == 10) || (isbn.length() == 13)) ? isbn : null;
    }

}
