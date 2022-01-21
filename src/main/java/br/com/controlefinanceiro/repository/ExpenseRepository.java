package br.com.controlefinanceiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.controlefinanceiro.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>{

}
