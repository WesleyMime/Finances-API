package br.com.finances.api.generic;

import br.com.finances.api.client.Client;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenericRepository<T> {

	Window<T> findFirst10ByClientOrderByDateDesc(Client client,
												 ScrollPosition position);

	Window<T> findFirst10ByDescriptionContainingIgnoreCaseAndClientOrderByDateDesc(String description, Client client,
																				   KeysetScrollPosition position);


	Optional<T> findByIdAndClient(Long id, Client client);

	Optional<T> findByDescriptionAndDate(String description, Integer year, Integer month,
										 Client client);

	List<T> findByYearAndMonth(Integer year, Integer month, Client client);

	void deleteById(long l);

	T save(T updated);

	List<T> saveList(Set<T> toAddList);
}
