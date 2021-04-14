package bookstore.api.bookstore.service.dto;

import bookstore.api.bookstore.enums.Role;
import bookstore.api.bookstore.persistence.entity.BookEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty(message = "userName must not be empty")
    private String username;
    @NotBlank(message = "first name must not be empty")
    private String firstName;
    @NotBlank(message = "last name must not be empty")
    private String lastName;
    @NotEmpty(message = "email must not be empty")
    @Email(message = "email should be a valid email")
    private String email;
    //    @Pattern(regexp = "^(?=.*[0-9]) (?=.*[a-z]) (?=.*[A-Z]) (?=.*[@#$%^&--_+=()]) (?=\\S+$).{8,30}",
    //            message = "The password must contain at least one digit, one upper case letter, " +
    //                    "one lower case letter, one special character which includes !@#$%&*()-+=^, " +
    //                    "and can not contain any white space")
    //    @Size(max = 30, min = 8, message = "The password must contain at least 8 characters and at most 20 characters")
    @NotEmpty(message = "password must not be empty")
    private String password;
    private Role role;
    @JsonIgnore
    private List<BookEntity> favoriteBooks;

}
