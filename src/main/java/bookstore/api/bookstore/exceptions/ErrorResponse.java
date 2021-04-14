package bookstore.api.bookstore.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-Apr-21
 */

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private List<String> details;

    public ErrorResponse(String message, List<String> details) {
        super();
        this.message = message;
        this.details = details;
    }
}
