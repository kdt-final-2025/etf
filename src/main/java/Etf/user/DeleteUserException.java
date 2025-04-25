package Etf.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DeleteUserException extends RuntimeException {
    public DeleteUserException(String message) {
        super(message);
    }
}
