package bookstore.api.bookstore.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * @author Tatevik Mirzoyan
 * Created on 16-Apr-21
 */
@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Role name can not be null or empty")
    private String roleName;

    public RoleEntity(String roleName) {
        this.roleName = roleName;
    }
}
