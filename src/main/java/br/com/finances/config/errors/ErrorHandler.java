package br.com.finances.config.errors;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErrorFormDTO> handle(MethodArgumentNotValidException exception) {
		List<ErrorFormDTO> errorFormDto = new ArrayList<>();
		
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		fieldErrors.forEach(e -> {
			errorFormDto.add(new ErrorFormDTO(e.getField(), e.getDefaultMessage()));
		});
		
		return errorFormDto;		
	}
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(EntityExistsException.class)
	public ErrorDTO handle(EntityExistsException exception) {
		return new ErrorDTO("There is already an entity with this description in this month registered.");
	}
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(EmailAlreadyRegisteredException.class)
	public ErrorDTO handle(EmailAlreadyRegisteredException exception) {
		return new ErrorDTO("There is already a client with this email registered.");
	}
}
