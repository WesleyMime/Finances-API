package br.com.finances.config.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.io.Serial;

public class EmailAlreadyRegisteredException extends ErrorResponseException {

	@Serial
	private static final long serialVersionUID = 312132320572629766L;

	public EmailAlreadyRegisteredException() {
		super(HttpStatus.CONFLICT);
	}
}
