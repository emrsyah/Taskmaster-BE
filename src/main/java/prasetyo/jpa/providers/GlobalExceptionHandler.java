package prasetyo.jpa.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

import prasetyo.jpa.helper.ResponseHelper;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseHelper responseHelper;

    public GlobalExceptionHandler(ResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.add(error.getDefaultMessage())
        );

        return responseHelper.error("Failed validation", errors, false, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return responseHelper.error(ex.getMessage(), null, false, HttpStatus.UNAUTHORIZED.value());
    }
}
