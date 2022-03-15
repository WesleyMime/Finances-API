package br.com.finances.api.generic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import br.com.finances.api.client.Client;

@NoRepositoryBean
public interface GenericRepository<T> extends JpaRepository<T, Long> {

	List<T> findByClient(Client client);

	Optional<T> findByDescriptionAndClient(String description, Client client);

	Optional<T> findByIdAndClient(Long id, Client client);

	Optional<T> findByDescriptionAndMonth(String description, Integer month, Client client);

	List<T> findByYearAndMonth(Integer year, Integer month, Client client);

}
