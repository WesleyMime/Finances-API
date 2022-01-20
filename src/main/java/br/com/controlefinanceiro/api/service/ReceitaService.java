package br.com.controlefinanceiro.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.controlefinanceiro.model.Receita;
import br.com.controlefinanceiro.repository.ReceitaRepository;

@Service
public class ReceitaService {

	public static List<Receita> getAll(ReceitaRepository receitasRepository) {
		List<Receita> listaReceitas = receitasRepository.findAll();
		return listaReceitas;
	}

}
