package br.com.finances.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.finances.model.Client;
import br.com.finances.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>{

	@Query("SELECT i FROM Income i WHERE i.description = ?1 AND EXTRACT(month FROM i.date) = ?2 AND i.client = ?3")
	// Extract: Get Month from LocalDate
	Optional<Income> findByDescriptionAndMonth(String description, Integer month, Client client);

	@Query("SELECT i FROM Income i WHERE EXTRACT(year FROM i.date) = ?1 AND EXTRACT(month FROM i.date) = ?2 AND i.client = ?3")
	List<Income> findByYearAndMonth(Integer year, Integer month, Client client);

	@Query("SELECT sum(i.value) FROM Income i WHERE year(i.date) = :year AND month(i.date) = :month AND i.client = :client")
	Optional<BigDecimal> totalIncomeMonth(Integer year, Integer month, Client client);

	List<Income> findByClient(Client client);

	Optional<Income> findByDescriptionAndClient(String description, Client client);

	Optional<Income> findByIdAndClient(Long id, Client client);
}
