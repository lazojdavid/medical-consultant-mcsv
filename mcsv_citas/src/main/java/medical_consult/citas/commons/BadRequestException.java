package medical_consult.citas.commons;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeCustomException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}