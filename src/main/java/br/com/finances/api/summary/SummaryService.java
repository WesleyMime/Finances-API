package br.com.finances.api.summary;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.ExpenseCategoryDTO;
import br.com.finances.api.expense.ExpenseDTO;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.IncomeDTO;
import br.com.finances.api.income.IncomeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SummaryService {

	private final IncomeRepository incomeRepository;

	private final ExpenseRepository expenseRepository;

	private final ClientRepository clientRepository;

	public SummaryService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository,
						  ClientRepository clientRepository) {
		this.incomeRepository = incomeRepository;
		this.expenseRepository = expenseRepository;
		this.clientRepository = clientRepository;
	}

	public Optional<SummaryDTO> getSummaryByDate(String yearString, String monthString) {
		Integer year;
		Integer month;
		Client client = getClient();

		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch (NumberFormatException _) {
			return Optional.empty();
		}

		BigDecimal totalIncome = incomeRepository.totalIncomeMonth(year, month, client).orElse(BigDecimal.ZERO);
		BigDecimal totalExpense = expenseRepository.totalExpenseMonth(year, month, client).orElse(BigDecimal.ZERO);

		BigDecimal finalBalance = totalIncome.subtract(totalExpense);
		List<ExpenseCategoryDTO> totalExpenseByCategory = expenseRepository.totalExpenseByCategory(year, month,
				client);

		return Optional.of(new SummaryDTO(totalIncome, totalExpense, finalBalance, totalExpenseByCategory));
	}

	@Cacheable(value = "summary-last-year", key = "#principal.name")
	public SummaryLastYearDTO getSummaryOfLastYear(LocalDate date, Principal principal) {
		Client client = getClient();

		LocalDate from = LocalDate.of(date.getYear() - 1, date.getMonthValue(), 1);
		List<IncomeDTO> incomeFromLastYear = incomeRepository.findIncomeFromLastYear(from, date.minusMonths(1),
				client);
		List<ExpenseDTO> expenseFromLastYear = expenseRepository.findExpenseFromLastYear(from, date.minusMonths(1),
				client);

		List<BigDecimal> finalBalanceEachMonth = new ArrayList<>();
		BigDecimal sumBalanceEachMonth = BigDecimal.ZERO;
		BigDecimal totalYearIncome = BigDecimal.ZERO;
		BigDecimal totalYearExpense =
				BigDecimal.ZERO;

		for (int i = 0; i < 12; i++) {
			BigDecimal monthIncome = incomeRepository.totalIncomeMonth(from.getYear(), from.getMonthValue(),
					client).orElse(BigDecimal.ZERO);
			BigDecimal monthExpenses = expenseRepository.totalExpenseMonth(from.getYear(), from.getMonthValue(),
					client).orElse(BigDecimal.ZERO);
			from = from.plusMonths(1);

			totalYearIncome = totalYearIncome.add(monthIncome);
			totalYearExpense = totalYearExpense.add(monthExpenses);

			BigDecimal balanceForMonth = monthIncome.subtract(monthExpenses);
			finalBalanceEachMonth.add(balanceForMonth);

			sumBalanceEachMonth = sumBalanceEachMonth.add(balanceForMonth);

		}
		BigDecimal avgBalanceYear = sumBalanceEachMonth.divide(BigDecimal.valueOf(12), RoundingMode.FLOOR);

		if (totalYearIncome.compareTo(BigDecimal.ZERO) == 0)
			return new SummaryLastYearDTO(totalYearIncome, totalYearExpense, avgBalanceYear, BigDecimal.ZERO,
					finalBalanceEachMonth, incomeFromLastYear, expenseFromLastYear);

		BigDecimal percentageSavingsRate =
				sumBalanceEachMonth.multiply(BigDecimal.valueOf(100)).divide(totalYearIncome,
						2, RoundingMode.HALF_EVEN);

		return new SummaryLastYearDTO(totalYearIncome, totalYearExpense, avgBalanceYear, percentageSavingsRate,
				finalBalanceEachMonth, incomeFromLastYear, expenseFromLastYear);
	}

	private Client getClient() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Client> client = clientRepository.findByEmail(email);
		return client.orElse(null);
	}
}
