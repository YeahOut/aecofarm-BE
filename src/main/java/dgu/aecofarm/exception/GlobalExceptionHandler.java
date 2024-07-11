package dgu.aecofarm.exception;

import dgu.aecofarm.entity.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<Response<?>> handleInvalidUserIdException(InvalidUserIdException ex) {
        return new ResponseEntity<>(Response.failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(Response.failure(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
