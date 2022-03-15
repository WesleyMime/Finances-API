package br.com.finances.api.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long>{

	Optional<Client> findByEmail(String username);
}
