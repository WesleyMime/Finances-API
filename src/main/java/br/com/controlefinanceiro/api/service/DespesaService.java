package br.com.controlefinanceiro.api.service;

import java.util.List;

import br.com.controlefinanceiro.model.Despesa;
import br.com.controlefinanceiro.repository.DespesaRepository;

public class DespesaService {

	public static List<Despesa> getAll(DespesaRepository despesaRepository) {
		List<Despesa> listaDespesas = despesaRepository.findAll();
		return listaDespesas;
	}

}
