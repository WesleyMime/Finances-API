package br.com.finances.api.generic;

import br.com.finances.api.client.Client;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T> {

	List<T> findByClient(Client client);

	Optional<T> findByDescriptionAndClient(String description, Client client);

	Optional<T> findByIdAndClient(Long id, Client client);

	Optional<T> findByDescriptionAndMonth(String description, Integer month, Client client);

	List<T> findByYearAndMonth(Integer year, Integer month, Client client);

	void deleteById(long l);

	T save(T updated);
}
