package br.com.finances.config.errors;

public class ErrorFormDTO {

	private String field;
	private String message;	
	
	public ErrorFormDTO(String field, String message) {
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
