package br.com.controlefinanceiro.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.controlefinanceiro.api.service.ReceitaService;
import br.com.controlefinanceiro.model.Receita;
import br.com.controlefinanceiro.repository.ReceitaRepository;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {
	
	@Autowired
	private ReceitaRepository receitaRepository;
	
	@GetMapping
	public ResponseEntity<List<Receita>> getAllReceitas(){
		List<Receita> receitas = ReceitaService.getAll(receitaRepository);
		return ResponseEntity.ok(receitas);
	}
}
