package bookstore.api.bookstore.service.dto;

import bookstore.api.bookstore.persistence.entity.BookEntity;
import bookstore.api.bookstore.persistence.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Tatevik Mirzoyan
 * Created on 14-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    @JsonIgnore
    private Long id;
    @NotNull
    @Size(max = 10, message = "Rate can be max 10")
    private Integer rate;
    private BookEntity book;
    private UserEntity userEntity;

}
