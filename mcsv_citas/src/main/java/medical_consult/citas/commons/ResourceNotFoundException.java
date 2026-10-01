package medical_consult.citas.commons;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeCustomException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}