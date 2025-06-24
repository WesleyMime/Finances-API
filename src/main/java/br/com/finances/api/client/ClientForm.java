package br.com.finances.api.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ClientForm(@Size(min = 3, max = 255) String name,
						 @Size(min = 5, max = 255) @Email String email,
						 @Size(min = 8, max = 255) String password) {
}
