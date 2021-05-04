package bookstore.api.bookstore.service.model.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-May-21
 */
@Getter
@Setter
public class Rate {
    @CsvBindByName(column = "User-ID")
    private Long userId;
    @CsvBindByName(column = "ISBN")
    private String isbn;
    @CsvBindByName(column = "Book-Rating")
    private int rate;

}
