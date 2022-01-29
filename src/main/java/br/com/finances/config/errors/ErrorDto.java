package br.com.finances.config.errors;

public class ErrorDto {

	private String field;
	private String message;	
	
	public ErrorDto(String field, String message) {
		this.field = field;
		this.message = message;
	}
	
	public String getField() {
		return field;
	}
	public String getMessage() {
		return message;
	}
	
	
}
