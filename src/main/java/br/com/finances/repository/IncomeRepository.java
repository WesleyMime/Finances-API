package br.com.finances.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.finances.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>{

	@Query("SELECT i FROM Income i WHERE i.description = ?1 AND EXTRACT(month FROM i.date) = ?2")
	// Extract: Get Month from LocalDate
	Optional<Income> findByDescriptionAndMonth(String description, Integer month);

	Optional<Income> findByDescription(String description);

	@Query("SELECT i FROM Income i WHERE EXTRACT(year FROM i.date) = ?1 AND EXTRACT(month FROM i.date) = ?2")
	List<Income> findByYearAndMonth(Integer year, Integer month);

	@Query("SELECT sum(value) FROM Income WHERE year(date) = :year AND month(date) = :month")
	Optional<BigDecimal> totalIncomeMonth(Integer year, Integer month);
}
