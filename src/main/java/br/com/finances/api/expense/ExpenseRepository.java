package br.com.finances.api.expense;

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
public interface ExpenseRepository extends GenericRepository<Expense>, JpaRepository<Expense, Long> {

	@Query("SELECT e FROM Expense e WHERE e.description = ?1 AND EXTRACT(year FROM e.date) = ?2 " +
			"AND EXTRACT(month FROM e.date) = ?3 AND e.client = ?4")
	Optional<Expense> findByDescriptionAndDate(String description, Integer year, Integer month,
											   Client client);

	@Query("SELECT e FROM Expense e WHERE EXTRACT(year from e.date) = ?1 AND EXTRACT(month FROM e.date) = ?2 AND " +
			"e.client = ?3 ORDER BY e.date DESC")
	List<Expense> findByYearAndMonth(Integer year, Integer month, Client client);

	@Query("SELECT sum(e.value) FROM Expense e WHERE year(e.date) = :year AND month(e.date) = :month AND e.client = " +
			":client")
	Optional<BigDecimal> totalExpenseMonth(Integer year, Integer month, Client client);

	@Query("SELECT new br.com.finances.api.expense.ExpenseCategoryDTO(category, SUM(value)) FROM Expense WHERE " +
			"year(date) = :year AND month(date) = :month AND client = :client GROUP BY category")
	List<ExpenseCategoryDTO> totalExpenseByCategory(Integer year, Integer month, Client client);

	@Query("SELECT new br.com.finances.api.expense.ExpenseDTO(i) FROM Expense i WHERE (i.date) BETWEEN ?1 AND ?2 " +
			"AND i.client = ?3")
	List<ExpenseDTO> findExpenseByDateBetween(LocalDate from, LocalDate to, Client client);

	@Query("SELECT sum(i.value) FROM Expense i WHERE (i.date) <= ?1 " +
			"AND i.client = ?2")
	BigDecimal sumAllExpenseUntilNow(LocalDate date, Client client);

	default List<Expense> saveList(Set<Expense> toAddList) {
		return saveAll(toAddList);
	}
}
