package medical_consult.citas.commons;

import org.springframework.http.HttpStatus;

public class RuntimeCustomException extends RuntimeException {

    private final HttpStatus status;

    public RuntimeCustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}