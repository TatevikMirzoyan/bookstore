package bookstore.api.bookstore.service.model.csv;

import bookstore.api.bookstore.persistence.entity.AuthorEntity;
import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 15-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private Long id;

    @CsvBindByName(column = "Book-Title")
    private String title;
//    @CsvBindByName(column = "genre")
//    private String genre;
//    @CsvBindByName(column = "price")
//    private Double price;
    @CsvBindByName(column = "ISBN")
    private String isbn;
    @CsvBindByName(column = "Year-Of-Publication")
    private Integer publishedYear;
    @CsvBindByName(column = "Publisher")
    private String publisher;
//    @CsvBindByName(column = "averageRate")
//    private Double averageRate;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = ",", column = "Book-Author")
    private List<String> authors;

    private List<Long> images;
}
