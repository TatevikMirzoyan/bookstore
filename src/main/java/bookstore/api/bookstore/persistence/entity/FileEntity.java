package bookstore.api.bookstore.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Tatevik Mirzoyan
 * Created on 01-Apr-21
 */
@Component
@ConfigurationProperties(prefix = "file")
@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private long size;
    private String extension;
    private LocalDate createdAt;
    private String uploadDir;

}
