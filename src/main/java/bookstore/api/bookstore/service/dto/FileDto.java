package bookstore.api.bookstore.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Tatevik Mirzoyan
 * Created on 14-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private Long id;
    private String name;
    private String type;
    private long size;
    private String extension;
    private LocalDate createdAt;
}
