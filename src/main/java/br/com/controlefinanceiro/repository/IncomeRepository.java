package br.com.controlefinanceiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.controlefinanceiro.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>{

}
