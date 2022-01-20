package br.com.controlefinanceiro.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.controlefinanceiro.api.service.DespesaService;
import br.com.controlefinanceiro.model.Despesa;
import br.com.controlefinanceiro.repository.DespesaRepository;

@RestController
@RequestMapping("/despesas")
public class DespesaController {

	@Autowired
	private DespesaRepository despesaRepository;
	
	@GetMapping
	public ResponseEntity<List<Despesa>> getAllDespesas(){
		List<Despesa> listaDespesas = DespesaService.getAll(despesaRepository);
		return ResponseEntity.ok(listaDespesas);
	}
}
