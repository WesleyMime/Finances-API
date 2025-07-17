package br.com.finances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FinancesApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancesApplication.class, args);
	}

}
