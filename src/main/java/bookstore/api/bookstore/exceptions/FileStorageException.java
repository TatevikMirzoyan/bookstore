package bookstore.api.bookstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-Apr-21
 */
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
