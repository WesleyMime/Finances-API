package br.com.finances.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.finances.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long>{

	Optional<Client> findByEmail(String username);
}
