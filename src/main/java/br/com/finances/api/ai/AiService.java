package br.com.finances.api.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AiService {

	private final ChatModel chatModel;

	private static final String INITIAL_PROMPT = """
			You are a financial advisor and you should give responses direct responses, without
			repeating the prompt and saying hello, using less than 500 characters, in portuguese
			and in a friendly and encouraging tone.
			""";
	public AiService(ChatModel chatModel) {
		this.chatModel = chatModel;
	}

	@Cacheable("monthOverMonthComparisonTakeaway")
	public ChatResponseDTO getMonthOverMonthComparisonTakeaway(String income, String expenses) {
		String prompt = INITIAL_PROMPT + """
				Analyze the user's financial balance difference of income and expenses for this
				month compared to the last and provide a summary of the key differences.
				The summary should include and explanation of the implications of these changes
				on the user's overall financial health.
				""";
		String data = "Income difference: " + income + ", Expenses difference: " + expenses;
		String response = this.chatModel.call(prompt + data);
		return new ChatResponseDTO(response);
	}

	@Cacheable("financialBalanceTakeaway")
	public ChatResponseDTO getFinancialBalanceTakeaway(String balanceEachMonth) {
		String prompt = INITIAL_PROMPT + """ 
				Analyze the user's financial data for the past year and provide a summary of their
				monthly financial balance. The summary should include:
				A statement of whether the user's income has consistently exceeded their expenses,
				resulting in a positive or negative cash flow (if the number is negative);
				A brief explanation of the implications of the user's financial balance on their
				overall financial health.
				""";
		String data = "Final Balance for each month, starting from " + LocalDate.now().minusMonths(
				12)
				+ " until " + LocalDate.now().minusMonths(1) + ": " + balanceEachMonth;
		String response = this.chatModel.call(prompt + data);
		return new ChatResponseDTO(response);
	}

	@Cacheable("spendingByCategoryLastMonthTakeaway")
	public ChatResponseDTO getSpendingByCategoryLastMonthTakeaway(String spendingByCategoryMonth) {
		String prompt = INITIAL_PROMPT + """  
				Analyze the user's expenses for the past month and provide a summary of their spending
				habits. The summary should include:
				A statement of which categories account for the largest portion of the user's spending
				A recommendation for reviewing and optimizing the user's spending in these categories
				to identify potential savings.
				""";
		String data = "Expenses for last month divided by category: " + spendingByCategoryMonth;
		String response = this.chatModel.call(prompt + data);
		return new ChatResponseDTO(response);
	}

	@Cacheable("spendingByCategoryYearTakeaway")
	public ChatResponseDTO getSpendingByCategoryYearTakeaway(String spendingByCategoryYear) {
		String prompt = INITIAL_PROMPT + """
				Analyze the user's expenses for the past year and provide a summary of their spending
				habits. The summary should include:
				A statement of which categories account for the largest portion of the user's spending
				A recommendation for reviewing and optimizing the user's spending in these categories
				to identify potential savings.
				""";
		String data =
				"Total expenses for last 12 months divided by category: " + spendingByCategoryYear;
		String response = this.chatModel.call(prompt + data);
		return new ChatResponseDTO(response);
	}

	@Cacheable("savingsTakeaway")
	public ChatResponseDTO getSavingsTakeaway(String savings) {
		String prompt = INITIAL_PROMPT + """ 
				Based on the user's current savings percentage, provide a concise summary of their
				savings habits.
				The summary should only include an evaluation of whether this percentage is above, at,
				or below the recommended average (which is 20%) and a brief explanation of what this
				indicates about the user's ability to save.
				""";
		String data = "Savings percentage for last 12 months: " + savings;
		String response = this.chatModel.call(prompt + data);
		return new ChatResponseDTO(response);
	}
}
