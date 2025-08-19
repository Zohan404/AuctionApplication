package elte.icj06o.auction.config;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(Exception exception) {
        Map<String, String> errors = new HashMap<>();

        errors.put("general", "Invalid e-mail or password.");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception exception) {
        Map<String, String> errors = new HashMap<>();

        if (exception.getMessage().contains("already registered")) {
            errors.put("email", exception.getMessage());
        } else if (exception.getMessage().contains("Existing password")) {
            errors.put("oldPassword", exception.getMessage());
        } else if (exception.getMessage().contains("New password and confirmation password")) {
            errors.put("newPassword", exception.getMessage());
            errors.put("confirmPassword", exception.getMessage());
        } else if (exception.getMessage().contains("Required part 'files' is not present")) {
            errors.put("pictures", "At least one picture has to be uploaded.");
        } else if (exception.getMessage().contains("Bid increment")) {
            errors.put("bidIncrement", exception.getMessage());
        } else if (exception.getMessage().contains("Buy now price")) {
            errors.put("buyNowPrice", exception.getMessage());
        } else if (exception.getMessage().contains("Start date must be before end date.")) {
            errors.put("startDate", exception.getMessage());
            errors.put("endDate", exception.getMessage());
        } else {
            errors.put("general", exception.getMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }
}
