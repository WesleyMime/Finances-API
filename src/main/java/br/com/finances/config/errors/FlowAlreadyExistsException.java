package br.com.finances.config.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class FlowAlreadyExistsException extends ErrorResponseException {
    public FlowAlreadyExistsException() {
        super(HttpStatus.CONFLICT);
    }
}
