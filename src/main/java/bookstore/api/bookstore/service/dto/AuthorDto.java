package bookstore.api.bookstore.service.dto;

import bookstore.api.bookstore.persistence.entity.BookEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 14-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    @JsonIgnore
    private Long id;
    @NotBlank(message = "Author name must not be empty")
    private String name;
    @JsonIgnore
    private List<BookEntity> books;

    public AuthorDto(String name) {
        this.name = name;
    }
}
