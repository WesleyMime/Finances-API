package br.com.finances.api.income;

import br.com.finances.api.client.Client;
import br.com.finances.api.generic.GenericRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IncomeRepository extends GenericRepository<Income>, JpaRepository<Income, Long> {
	@Query("SELECT i FROM Income i WHERE i.description = ?1 AND EXTRACT(year FROM i.date) = ?2 AND" +
			" EXTRACT(month FROM i.date) = ?3 AND i.client = ?4")
	Optional<Income> findByDescriptionAndDate(String description, Integer year, Integer month,
											  Client client);

	@Query("SELECT i FROM Income i WHERE EXTRACT(year FROM i.date) = ?1 AND EXTRACT(month FROM i.date) = ?2 AND i" +
			".client = ?3 ORDER BY i.date DESC")
	List<Income> findByYearAndMonth(Integer year, Integer month, Client client);

	@Query("SELECT sum(i.value) FROM Income i WHERE year(i.date) = :year AND month(i.date) = :month AND i.client = " +
			":client")
	Optional<BigDecimal> totalIncomeMonth(Integer year, Integer month, Client client);

	@Query("SELECT new br.com.finances.api.income.IncomeDTO(i) FROM Income i WHERE (i.date) BETWEEN ?1 AND ?2 " +
			"AND i.client = ?3 ORDER BY i.date ASC")
	List<IncomeDTO> findIncomeByDateBetween(LocalDate from, LocalDate to, Client client);

	@Query("SELECT new br.com.finances.api.income.IncomeDTO(i) FROM Income i WHERE (i.date) <= ?1 " +
			"AND i.client = ?2")
	List<IncomeDTO> findAllIncomeUntilNow(LocalDate date, Client client);

	default List<Income> saveList(Set<Income> toAddList) {
		return saveAll(toAddList);
	}
}
