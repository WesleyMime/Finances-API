package br.com.finances.api.generic;

import br.com.finances.api.client.Client;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenericRepository<T> {

	Window<T> findByClientOrderByDateDesc(Client client,
										  ScrollPosition position, Limit limit);

	Window<T> findByDescriptionContainingIgnoreCaseAndClientOrderByDateDesc(String description, Client client,
																			KeysetScrollPosition position,
																			Limit limit);

	List<T> findFirst5ByClientAndDateLessThanEqualOrderByDateDesc(Client client, LocalDate date);

	Optional<T> findByIdAndClient(Long id, Client client);

	List<T> findByYearAndMonth(Integer year, Integer month, Client client);

	void deleteById(long l);

	T save(T updated);

	List<T> saveList(Set<T> toAddList);
}
