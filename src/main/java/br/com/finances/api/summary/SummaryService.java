package br.com.finances.api.summary;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.ExpenseCategoryDTO;
import br.com.finances.api.expense.ExpenseDTO;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.Income;
import br.com.finances.api.income.IncomeDTO;
import br.com.finances.api.income.IncomeDtoMapper;
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

	private final IncomeDtoMapper dtoMapper;

	public SummaryService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository,
						  ClientRepository clientRepository, IncomeDtoMapper dtoMapper) {
		this.incomeRepository = incomeRepository;
		this.expenseRepository = expenseRepository;
		this.clientRepository = clientRepository;
		this.dtoMapper = dtoMapper;
	}

	@Cacheable(value = "summary-by-month", key = "#principal.name.concat(#yearString).concat(#monthString)",
			unless = "#result == null")
	public Optional<SummaryDTO> getSummaryByMonth(String yearString, String monthString, Principal principal) {
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

		List<Income> incomeList = incomeRepository.findByYearAndMonth(year, month, client);
		List<ExpenseCategoryDTO> totalExpenseByCategory = expenseRepository.totalExpenseByCategory(year, month,
				client);

		List<IncomeDTO> incomeListDto = incomeList.stream().map(dtoMapper::map).toList();
		return Optional.of(
				new SummaryDTO(totalIncome, totalExpense, totalIncome.subtract(totalExpense), incomeListDto,
						totalExpenseByCategory));
	}

	@Cacheable(value = "summary-last-year", key = "#principal.name.concat(#date.year).concat(#date.month)",
			unless = "#result == null")
	public SummaryLastYearDTO getSummaryOfLastYear(LocalDate date, Principal principal) {
		Client client = getClient();

		LocalDate from = LocalDate.of(date.getYear() - 1, date.getMonthValue(), 1);
		LocalDate now = LocalDate.now();
		List<IncomeDTO> incomeFromLastYear = incomeRepository.findIncomeByDate(from,
				now.minusDays(now.getDayOfMonth()),
				client);
		List<ExpenseDTO> expenseFromLastYear = expenseRepository.findExpenseByDate(from,
				now.minusDays(now.getDayOfMonth()),
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

	@Cacheable(value = "summary-by-date", key = "#principal.name.concat(#yearFromString).concat(#yearToString)",
			unless = "#result == null")
	public Optional<SummaryBasicDTO> getSummaryByDate(String yearFromString, String monthFromString,
													  String yearToString, String monthToString, Principal principal) {
		Integer yearFrom;
		Integer monthFrom;
		Integer yearTo;
		Integer monthTo;
		Client client = getClient();

		try {
			yearFrom = Integer.parseInt(yearFromString);
			monthFrom = Integer.parseInt(monthFromString);
			yearTo = Integer.parseInt(yearToString);
			monthTo = Integer.parseInt(monthToString);
		} catch (NumberFormatException _) {
			return Optional.empty();
		}

		LocalDate from = LocalDate.of(yearFrom, monthFrom, 1);
		// Get the last day of the 'to' month
		LocalDate to = LocalDate.of(yearTo, monthTo + 1, 1).minusDays(1);
		List<IncomeDTO> incomeListByDate = incomeRepository.findIncomeByDate(from, to, client);
		List<ExpenseDTO> expenseListByDate = expenseRepository.findExpenseByDate(from, to, client);

		BigDecimal balance;
		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalExpense = BigDecimal.ZERO;

		for (IncomeDTO incomeDTO : incomeListByDate) {
			totalIncome = totalIncome.add(incomeDTO.getValue());
		}
		for (ExpenseDTO expenseDTO : expenseListByDate) {
			totalExpense = totalExpense.add(expenseDTO.getValue());
		}

		balance = totalIncome.subtract(totalExpense);

		return Optional.of(new SummaryBasicDTO(totalIncome, totalExpense, balance));
	}

	@Cacheable(value = "account-summary", key = "#principal.name.concat(#date.month)",
			unless = "#result == null")
	public SummaryBasicDTO getAccountSummary(LocalDate date, Principal principal) {
		Client client = getClient();

		List<IncomeDTO> incomeList = incomeRepository.findAllIncomeUntilNow(date, client);
		List<ExpenseDTO> expenseList = expenseRepository.findAllExpenseUntilNow(date, client);

		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalExpense = BigDecimal.ZERO;

		for (IncomeDTO incomeDTO : incomeList) {
			totalIncome = totalIncome.add(incomeDTO.getValue());
		}
		for (ExpenseDTO expenseDTO : expenseList) {
			totalExpense = totalExpense.add(expenseDTO.getValue());
		}
		return new SummaryBasicDTO(totalIncome, totalExpense, totalIncome.subtract(totalExpense));
	}

	private Client getClient() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Client> client = clientRepository.findByEmail(email);
		return client.orElse(null);
	}
}
