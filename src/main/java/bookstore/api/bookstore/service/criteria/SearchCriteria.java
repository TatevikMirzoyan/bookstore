package bookstore.api.bookstore.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private Integer page;
    private Integer size;

    private String sortField;
    private String sortDirection;

    public Pageable createPageRequest() {
        if (size != null && size == Integer.MAX_VALUE) {
            return null;
        }
        int page = this.page == null ? 0 : this.page;
        int size = this.size == null ? 10 : this.size;
        if (this.sortField == null || this.sortField.equals("")) {
            return PageRequest.of(page, size);
        } else {
            String dir = (this.sortDirection == null || !this.sortDirection.equalsIgnoreCase("DESC")) ? "ASC" : "DESC";
            return PageRequest.of(page, size, Sort.Direction.valueOf(dir), sortField);
        }

    }
}
