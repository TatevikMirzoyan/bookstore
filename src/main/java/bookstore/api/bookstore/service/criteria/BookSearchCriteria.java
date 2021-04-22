package bookstore.api.bookstore.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Tatevik Mirzoyan
 * Created on 28-Mar-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchCriteria extends SearchCriteria{
    private String title;
    private String genre;
    private Double minPrice;
    private String isbn;
    private Integer publishedYear;
    private String publisher;
    private Double minRate;
    private String author;

}
