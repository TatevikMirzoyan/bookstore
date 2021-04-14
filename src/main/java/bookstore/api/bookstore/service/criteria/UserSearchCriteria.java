package bookstore.api.bookstore.service.criteria;

import bookstore.api.bookstore.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Tatevik Mirzoyan
 * Created on 28-Mar-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria extends SearchCriteria{
    private String firstName;
    private String lastName;
    private Role role;

}
