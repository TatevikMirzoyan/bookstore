package bookstore.api.bookstore.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Tatevik Mirzoyan
 * Created on 02-Apr-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorSearchCriteria extends SearchCriteria {
    private String name;
}
