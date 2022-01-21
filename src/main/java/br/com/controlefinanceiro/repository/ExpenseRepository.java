package br.com.controlefinanceiro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.controlefinanceiro.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>{

	@Query("SELECT e FROM Expense e WHERE e.description = ?1 AND EXTRACT(MONTH from e.date) = ?2")
	// Extract: Get Month from LocalDate
	Optional<Expense> findByDescriptionAndMonth(String description, Integer month);
}
