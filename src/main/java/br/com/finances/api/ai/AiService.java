package br.com.finances.api.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AiService {

	private final ChatClient chatClient;
	private final ChatOptions chatOptions;

	private static final String INITIAL_PROMPT = """
			You are a financial advisor and you should give responses direct responses, without saying hello,
			repeating the prompt or explaining what you going to analyze, no need to offer help, using less than
			500 characters, in portuguese and in a friendly and encouraging tone.
			""";

	public AiService(ChatClient.Builder chatClientBuilder) {
		chatOptions = OpenAiChatOptions.builder()
				.temperature(0.3)
				.build();
		this.chatClient = chatClientBuilder.build();
	}

	@Cacheable("monthOverMonthComparisonTakeaway")
	public ChatResponseDTO getMonthOverMonthComparisonTakeaway(String income, String expenses) {
		String prompt = """
				Analyze the user's financial balance difference of income and expenses for this
				month compared to the last and provide a summary of the key differences.
				The summary should include and explanation of the implications of these changes
				on the user's overall financial health.
				""";
		String data = "Income difference: " + income + ", Expenses difference: " + expenses;
		String response = this.chatClient.prompt(prompt + data)
				.options(chatOptions)
				.system(INITIAL_PROMPT)
				.call()
				.content();
		assert response != null;
		String finalResponse = removeThinking(response);
		return new ChatResponseDTO(finalResponse);
	}

	//	@Cacheable("financialBalanceTakeaway")
	public ChatResponseDTO getFinancialBalanceTakeaway(String balanceEachMonth) {
		String prompt = """ 
				Analyze the user's financial data for the past year and provide a summary of their
				monthly financial balance. The summary should include a brief explanation of the implications of
				the user's financial balance on their overall financial health. Don't include the average in the
				response, don't repeat the numbers and every positive number is a positive balance.
				""";
		String data =
				"Final Balance for each month, separated by commas, starting from " + LocalDate.now().minusMonths(
				12) + " until " + LocalDate.now().minusMonths(1) + ": " + balanceEachMonth;
		String response = this.chatClient.prompt(prompt + data)
				.options(chatOptions)
				.system(INITIAL_PROMPT)
				.call()
				.content();
		assert response != null;
		String finalResponse = removeThinking(response);
		return new ChatResponseDTO(finalResponse);
	}

	@Cacheable("spendingByCategoryLastMonthTakeaway")
	public ChatResponseDTO getSpendingByCategoryLastMonthTakeaway(String spendingByCategoryMonth) {
		String prompt = """  
				Analyze the user's expenses for the past month and provide a summary of their spending
				habits. The summary should include:
				A statement of which categories account for the largest portion of the user's spending
				A recommendation for reviewing and optimizing the user's spending in these categories
				to identify potential savings.
				""";
		String data = "Expenses for last month divided by category: " + spendingByCategoryMonth;
		String response = this.chatClient.prompt(prompt + data)
				.options(chatOptions)
				.system(INITIAL_PROMPT)
				.call()
				.content();
		assert response != null;
		String finalResponse = removeThinking(response);
		return new ChatResponseDTO(finalResponse);
	}

	@Cacheable("spendingByCategoryYearTakeaway")
	public ChatResponseDTO getSpendingByCategoryYearTakeaway(String spendingByCategoryYear) {
		String prompt = """
				Analyze the user's expenses for the past year and provide a summary of their spending
				habits. The summary should include:
				A statement of which categories account for the largest portion of the user's spending
				A recommendation for reviewing and optimizing the user's spending in these categories
				to identify potential savings. Don't use ** to bold words.
				""";
		String data = "Total expenses for last 12 months divided by category: " + spendingByCategoryYear;
		String response = this.chatClient.prompt(prompt + data)
				.options(chatOptions)
				.system(INITIAL_PROMPT)
				.call()
				.content();
		assert response != null;
		String finalResponse = removeThinking(response);
		return new ChatResponseDTO(finalResponse);
	}

	@Cacheable("savingsTakeaway")
	public ChatResponseDTO getSavingsTakeaway(String savings) {
		String prompt = """ 
				Based on the user's current savings percentage, provide a concise summary of their
				savings habits.
				The summary should only include an evaluation of whether this percentage is above, at,
				or below the recommended average (which is 20%) and a brief explanation of what this
				indicates about the user's ability to save.
				""";
		String data = "Savings percentage for last 12 months: " + savings;
		String response = this.chatClient.prompt(prompt + data)
				.options(chatOptions)
				.system(INITIAL_PROMPT)
				.call()
				.content();
		assert response != null;
		String finalResponse = removeThinking(response);
		return new ChatResponseDTO(finalResponse);
	}

	public ChatResponseDTO getJSONForTransactionsUsingAI(String info, String type) {
		// Few-shot prompting
		String prompt = """
				Parse a user's transaction into valid JSON
				
				EXAMPLE 1:
				Adicione salário de 5440 na data de 5 de março de 2024.
				JSON Response:
				[
					{
						"description": "Salário",
						"value": "5440",
						"date": "2024-03-05"
					}
				]
				EXAMPLE 2:
				120 Supermercado no dia 1 e 45 no dia 10 de maio de 2024.
				JSON Response:
				[
					{
					 "description": "Supermercado",
					 "value": "120",
					 "date": "2024-05-01",
					 "category": "FOOD"
					},
					{
					 "description": "Farmácia",
					 "value": "45",
					 "date": "2024-05-10",
					 "category": "HEALTH"
					}
				]
				
				EXAMPLE 2:
				Parcelas de 299 da faculdade por 3 meses a partir de agosto de 2024.
				JSON Response:
				[
					{
					 "description": "Faculdade",
					 "value": "299",
					 "date": "2024-08-01",
					 "category": "EDUCATION"
					},
					{
					 "description": "Faculdade",
					 "value": "299",
					 "date": "2024-09-01",
					 "category": "EDUCATION"
					},
					{
					 "description": "Faculdade",
					 "value": "299",
					 "date": "2024-10-01",
					 "category": "EDUCATION"
					}
				]
				Category options:
					FOOD,
					HEALTH,
					HOME,
					TRANSPORT,
					EDUCATION,
					LEISURE,
					UNFORESEEN,
					OTHERS,
					SERVICES,
					CLOTHES
				""";
		LocalDate now = LocalDate.now();
		String response = this.chatClient
				.prompt(prompt + " " + info + " " + type + " Today is: " + now)
				.options(chatOptions)
				.system(INITIAL_PROMPT)
				.call()
				.content();
		assert response != null;
		String finalResponse = removeThinking(response);
		return new ChatResponseDTO(finalResponse);
	}

	private static String removeThinking(String response) {
		int indexFinalOfThinkSection = response.lastIndexOf(">"); // </think>
		return response.substring(indexFinalOfThinkSection + 3); // 3 = >\n
	}
}
