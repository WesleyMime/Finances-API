package br.com.finances.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.finances.dto.ExpenseCategoryDTO;
import br.com.finances.model.Client;
import br.com.finances.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>{
	
	@Query("SELECT e FROM Expense e WHERE e.description = ?1 AND EXTRACT(month FROM e.date) = ?2 AND e.client = ?3")
	// Extract: Get Month from LocalDate
	Optional<Expense> findByDescriptionAndMonth(String description, Integer month, Client client);
	
	@Query("SELECT e FROM Expense e WHERE EXTRACT(year from e.date) = ?1 AND EXTRACT(month FROM e.date) = ?2 AND e.client = ?3")
	List<Expense> findByYearAndMonth(Integer year, Integer month, Client client);

	@Query("SELECT sum(e.value) FROM Expense e WHERE year(e.date) = :year AND month(e.date) = :month AND e.client = :client")
	Optional<BigDecimal> totalExpenseMonth(Integer year, Integer month, Client client);

	@Query("SELECT new br.com.finances.dto.ExpenseCategoryDTO(category, SUM(value)) FROM Expense WHERE year(date) = :year AND month(date) = :month AND client = :client GROUP BY category" )
	List<ExpenseCategoryDTO> totalExpenseByCategory(Integer year, Integer month, Client client);

	List<Expense> findByClient(Client client);

	Optional<Expense> findByDescriptionAndClient(String description, Client client);

	Optional<Expense> findByIdAndClient(Long id, Client client);
}
