package br.com.controlefinanceiro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.controlefinanceiro.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>{

	@Query("SELECT i FROM Income i WHERE i.description = ?1 AND EXTRACT(MONTH from i.date) = ?2")
	// Extract: Get Month from LocalDate
	Optional<Income> findByDescriptionAndMonth(String description, Integer month);
}
