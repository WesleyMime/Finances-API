package br.com.finances.api.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/ai")
public class AIController {

	private final AiService aiService;

	public AIController(AiService aiService) {
		this.aiService = aiService;
	}

	@GetMapping("/monthOverMonth")
	public ResponseEntity<ChatResponseDTO> getMonthOverMonthComparisonTakeaway(
			@RequestParam(name = "income") String income, @RequestParam(name = "expenses") String expenses) {
		ChatResponseDTO response = this.aiService.getMonthOverMonthComparisonTakeaway(income, expenses);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/financialBalance")
	public ResponseEntity<ChatResponseDTO> getFinancialBalanceTakeaway(@RequestBody String balanceEachMonth) {
		ChatResponseDTO response = this.aiService.getFinancialBalanceTakeaway(balanceEachMonth);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/spendingByCategoryLastMonth")
	public ResponseEntity<ChatResponseDTO> getSpendingByCategoryLastMonthTakeaway(
			@RequestBody String spendingByCategoryMonth) {
		ChatResponseDTO response = this.aiService.getSpendingByCategoryLastMonthTakeaway(spendingByCategoryMonth);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/spendingByCategoryYear")
	public ResponseEntity<ChatResponseDTO> getSpendingByCategoryYearTakeaway(
			@RequestBody String spendingByCategoryYear) {
		ChatResponseDTO response = this.aiService.getSpendingByCategoryYearTakeaway(spendingByCategoryYear);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/savings")
	public ResponseEntity<ChatResponseDTO> getSavingsTakeaway(
			@RequestParam(name = "savingsPercentage") String savings) {
		ChatResponseDTO response = this.aiService.getSavingsTakeaway(savings);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/jsonForTransactions")
	public ResponseEntity<ChatResponseDTO> getJSONForTransactionsUsingAI(@RequestBody String info,
																		 @RequestParam("type") String type) {
		ChatResponseDTO response = this.aiService.getJSONForTransactionsUsingAI(info, type);
		return ResponseEntity.ok(response);
	}
}
