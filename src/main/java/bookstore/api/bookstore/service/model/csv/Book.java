package bookstore.api.bookstore.service.model.csv;

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
    private String genre;
    private Double price;
    @CsvBindByName(column = "ISBN")
    private String isbn;
    @CsvBindByName(column = "Year-Of-Publication")
    private Integer publishedYear;
    @CsvBindByName(column = "Publisher")
    private String publisher;
    private Double averageRate;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = ",", column = "Book-Author")
    private List<String> authors;

    @CsvBindByName(column = "Image-URL-S")
    private String imageURL;

}
