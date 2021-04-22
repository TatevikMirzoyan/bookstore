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

    public void addRate(RateEntity rate) {
        if (rates == null) {
            rates = new ArrayList<>();
        }
        this.rates.add(rate);
        composeAverageRate(rates);
    }

    public void removeRate(RateEntity rate) {
        if (rates != null) {
            this.rates.remove(rate);
            composeAverageRate(rates);
        }
    }

    public void addGenre(String genre) {
        if (genres == null) {
            genres = new ArrayList<>();
        }
        this.genres.add(genre);
    }

    public void removeGenre(String genre) {
        if (genres != null) {
            this.genres.remove(genre);
        }
    }

    public void addAuthor(AuthorEntity author) {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        this.authors.add(author);
    }

    public void removeAuthor(AuthorEntity author) {
        if (authors != null) {
            this.authors.remove(author);
        }
    }

    public void addImage(FileEntity file) {
        if (images == null) {
            images = new ArrayList<>();
        }
        this.images.add(file);
    }

    public void removeImage(FileEntity file) {
        if (images != null) {
            this.images.remove(file);
        }
    }


    public void composeAverageRate(List<RateEntity> rates) {
        if (rates != null) {
            this.rates.addAll(rates);
            if (this.rates.size() != 0) {
                DecimalFormat df = new DecimalFormat("#.##");
                this.averageRate = Double.valueOf(df.format((this.rates.stream()
                        .mapToDouble(RateEntity::getRate)
                        .sum() / this.rates.size())));
            }
        }else averageRate = 0.0;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
    }

    public void setIsbn(String isbn) {
        isbn = isbn.replaceAll("-", "");
        this.isbn = ((isbn.length() == 10) || (isbn.length() == 13)) ? isbn : null;
    }

}
