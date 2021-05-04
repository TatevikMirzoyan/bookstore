package bookstore.api.bookstore.service.model.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-Apr-21
 */

@Getter
@Setter
public class ErrorResponseWrapper {
    private String message;
    private List<String> details;

    public ErrorResponseWrapper(String message, List<String> details) {
        super();
        this.message = message;
        this.details = details;
    }
}
