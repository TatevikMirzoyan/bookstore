package bookstore.api.bookstore.service.model.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Getter
@Setter
public class PageResponseWrapper<T> {
    private Long total;
    private Integer totalPages;
    private List<T> data;

    public PageResponseWrapper(Long total, Integer totalPages, List<T> data) {
        this.total = total;
        this.totalPages = totalPages;
        this.data = data;
    }
}
