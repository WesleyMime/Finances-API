package br.com.finances.config.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		ProblemDetail problemDetail = e.getBody();
		problemDetail.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
		List<FieldErrorDetail> errors = new ArrayList<>();
		e.getFieldErrors().forEach(fieldError ->
				errors.add(new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage())));
		problemDetail.setProperty("errors", errors);
		return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
	}

	@ExceptionHandler(FlowAlreadyExistsException.class)
	public ResponseEntity<ProblemDetail> flowAlreadyExistsExceptionHandler(FlowAlreadyExistsException e) {
		e.setTitle("Flow already registered.");
		e.setDetail("There is already an income or expense with this description in this month registered.");
		return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
	}

	@ExceptionHandler(EmailAlreadyRegisteredException.class)
	public ResponseEntity<ProblemDetail> emailAlreadyRegisteredExceptionHandler(EmailAlreadyRegisteredException e) {
		e.setTitle("Email already registered.");
		e.setDetail("There is already a client with this email registered.");
		return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ProblemDetail> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problemDetail.setDetail("Documentation: https://documenter.getpostman.com/view/19203694/UVeGs6cv");
		return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ProblemDetail> runtimeExceptionHandler(RuntimeException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		problemDetail.setTitle("Something went wrong :( try again later. " +
				"Documentation: https://documenter.getpostman.com/view/19203694/UVeGs6cv");
		problemDetail.setDetail(e.getMessage());
		return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
	}

	private record FieldErrorDetail(String field, String detail) {
	}
}
