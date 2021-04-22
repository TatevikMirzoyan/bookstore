package bookstore.api.bookstore.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author Tatevik Mirzoyan
 * Created on 14-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherDto {
    @JsonIgnore
    private Long id;
    @NotBlank(message = "Publisher name must not be empty")
    private String name;

    public PublisherDto(String name) {
        this.name = name;
    }
}
