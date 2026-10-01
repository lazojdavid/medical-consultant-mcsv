package medical_consult.citas.commons;

import org.springframework.http.HttpStatus;

public class ConflictException extends RuntimeCustomException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}