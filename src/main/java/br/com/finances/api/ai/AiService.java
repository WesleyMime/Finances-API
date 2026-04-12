package br.com.finances.api.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;

@Service
public class AiService {

	private final ChatClient chatClient;
	private final ChatOptions chatOptions;

	private static final String INITIAL_PROMPT = """
			You are a financial advisor and you should give responses direct responses, without saying hello,
			repeating the prompt or explaining what you going to analyze, no need to offer help, using less than
			1000 characters, format only with bold and line breaks, don't use emotes, in Portuguese,
			in a friendly and encouraging tone for people that may not have a lot of financial knowledge.
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
		return callAI(prompt + data);
	}

	@Cacheable("financialBalanceTakeaway")
	public ChatResponseDTO getFinancialBalanceTakeaway(String balanceEachMonth) {
		String prompt = """ 
				Analyze the user's financial data for the past 12 months, provide a summary of their
				monthly financial balance. The summary should include a brief explanation of the implications of
				the user's financial balance on their overall financial health.
				Additionally, based on this historical data, provide a short-term (6-12 month) financial forecast.  This forecast should:
				
				1.  Identify Key Trends:  Analyze the monthly balances to identify patterns - are there seasonal fluctuations?
				Are balances consistently increasing or decreasing?  Are there specific months that consistently show a surplus or deficit?
				
				2.  Project Future Balances:  Using these trends, create a projected balance for the next 6-12 months.
				This projection should be presented as a range rather than a fixed number to account for potential changes.
				(e.g., "Based on recent trends, the user’s balance is projected to range between $X and $Y over the next six months.")
				
				3.  Highlight Potential Risks & Opportunities: Based on the forecast and the historical data, identify potential risks
				(e.g., extended periods of deficit, large drops in balance) and opportunities (e.g., consistent surpluses that could be invested).
				
				4.  Offer Preliminary Recommendations: Suggest 1-2 high-level recommendations based on the forecast and risk/opportunity assessment.
				These recommendations should be tailored to the situation revealed in the data (e.g., "Given the projected deficits,
				consider reducing discretionary spending" or "Given the projected surpluses, explore low-risk investment options.").
				
				The overall response should provide a clear assessment of the user's financial health today and a reasonable,
				data-driven expectation of their financial situation in the near future.
				Should not exceed 1000 characters.
				""";

		LocalDate date = LocalDate.now().minusMonths(12);
		String[] balance = balanceEachMonth.split(",");
		for (int i = 0; i < balance.length; i++) {
			balance[i] = balance[i].concat(" for " + date.getMonth() + " " + date.getYear());
			date = date.plusMonths(1);
		}
		String data = "Final Balance for each month: " + Arrays.toString(balance);
		return callAI(prompt + data);
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
		return callAI(prompt + data);
	}

	@Cacheable("spendingByCategoryYearTakeaway")
	public ChatResponseDTO getSpendingByCategoryYearTakeaway(String spendingByCategoryYear) {
		String prompt = """
				Analyze the user's expenses for the past year and provide a summary of their spending
				habits. The summary should include:
				A statement of which categories account for the largest portion of the user's spending
				A recommendation for reviewing and optimizing the user's spending in these categories
				to identify potential savings.
				""";
		String data = "Total expenses for last 12 months divided by category: " + spendingByCategoryYear;
		return callAI(prompt + data);
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
		return callAI(prompt + data);
	}

	public ChatResponseDTO getJSONForTransactionsUsingAI(String request, String transactionType) {
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
				120 Supermercado no dia 1 e 45,99 na farmacia no dia 10 de maio de 2024.
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
					 "value": "45.99",
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
		return callAI(String.format("%s %s %s Today is: %s", prompt, request, transactionType, now));
	}

	private ChatResponseDTO callAI(String prompt) {
		String response = this.chatClient
				.prompt(prompt)
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
