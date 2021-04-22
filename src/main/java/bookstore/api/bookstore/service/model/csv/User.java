package bookstore.api.bookstore.service.model.csv;

import bookstore.api.bookstore.persistence.entity.BookEntity;
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
public class User {

    private Long id;

    @CsvBindByName(column = "User Name")
    private String username;
    @CsvBindByName(column = "First Name")
    private String firstName;
    @CsvBindByName(column = "Last Name")
    private String lastName;
    @CsvBindByName(column = "E Mail")
    private String email;
    @CsvBindByName(column = "Password")
    private String password;

    private List<BookEntity> favoriteBooks;

    private List<Long> images;

}
