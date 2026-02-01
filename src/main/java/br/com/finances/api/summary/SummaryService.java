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
import java.time.format.DateTimeFormatter;
import java.util.*;

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
		List<IncomeDTO> incomeFromLastYear = incomeRepository.findIncomeByDateBetween(from,
				now.minusDays(now.getDayOfMonth()),
				client);
		List<ExpenseDTO> expenseFromLastYear = expenseRepository.findExpenseByDateBetween(from,
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

	@Cacheable(value = "summary-by-date", key = "#principal.name.concat(#yearFromString).concat(#monthFromString)" +
			".concat(#yearToString).concat(#monthToString)", unless = "#result == null")
	public SummaryPeriodDTO getSummaryByDate(String yearFromString, String monthFromString,
												   String yearToString, String monthToString, Principal principal) {
		int yearFrom;
		int monthFrom;
		int yearTo;
		int monthTo;
		Client client = getClient();

		try {
			yearFrom = Integer.parseInt(yearFromString);
			monthFrom = Integer.parseInt(monthFromString);
			yearTo = Integer.parseInt(yearToString);
			monthTo = Integer.parseInt(monthToString);
		} catch (NumberFormatException _) {
			return null;
		}

		LocalDate from = LocalDate.of(yearFrom, monthFrom, 1);
		// Get the last day of the 'to' month
		LocalDate to = LocalDate.of(yearTo, monthTo, 1).plusMonths(1).minusDays(1);

		List<IncomeDTO> incomeListByDate = incomeRepository.findIncomeByDateBetween(from, to, client);
		List<ExpenseDTO> expenseListByDate = expenseRepository.findExpenseByDateBetween(from, to, client);

		Map<String, SummaryBasicDTO> monthsMap = new HashMap<>();

		BigDecimal totalIncomePeriod = BigDecimal.ZERO;
		BigDecimal totalExpensePeriod = BigDecimal.ZERO;
		BigDecimal totalBalancePeriod = BigDecimal.ZERO;

		for (IncomeDTO incomeDTO : incomeListByDate) {
			String dateString = getDateFormatted(incomeDTO.getDate());

			SummaryBasicDTO summaryBasicDTO = monthsMap.getOrDefault(dateString, new SummaryBasicDTO());
			summaryBasicDTO.increaseTotalIncome(incomeDTO.getValue());
			summaryBasicDTO.increaseTotalBalance(incomeDTO.getValue());
			monthsMap.put(dateString, summaryBasicDTO);

			totalIncomePeriod = totalIncomePeriod.add(incomeDTO.getValue());
			totalBalancePeriod = totalBalancePeriod.add(incomeDTO.getValue());
		}

		for (ExpenseDTO expenseDTO : expenseListByDate) {
			String dateString = getDateFormatted(expenseDTO.getDate());

			SummaryBasicDTO summaryBasicDTO = monthsMap.getOrDefault(dateString, new SummaryBasicDTO());
			summaryBasicDTO.increaseTotalExpense(expenseDTO.getValue());
			summaryBasicDTO.decreaseTotalBalance(expenseDTO.getValue());
			monthsMap.put(dateString, summaryBasicDTO);

			totalExpensePeriod = totalExpensePeriod.add(expenseDTO.getValue());
			totalBalancePeriod = totalBalancePeriod.subtract(expenseDTO.getValue());
		}
		List<SummaryByDateDTO> list = new ArrayList<>(monthsMap.size());

		// Return list ordered by date with summary from month
		LocalDate date = from;
		while (date.isBefore(to)) {
			String dateString = getDateFormatted(date);
			list.add(new SummaryByDateDTO(dateString, monthsMap.getOrDefault(dateString, new SummaryBasicDTO())));
			date = date.plusMonths(1);
		}
		return new SummaryPeriodDTO(totalIncomePeriod, totalExpensePeriod, totalBalancePeriod, list);
	}

	@Cacheable(value = "account-summary", key = "#principal.name.concat(#date.month)",
			unless = "#result == null")
	public SummaryBasicDTO getAccountSummary(LocalDate date, Principal principal) {
		Client client = getClient();

		BigDecimal totalIncome = incomeRepository.sumAllIncomeUntilNow(date, client).orElse(BigDecimal.ZERO);
		BigDecimal totalExpense = expenseRepository.sumAllExpenseUntilNow(date, client).orElse(BigDecimal.ZERO);
		return new SummaryBasicDTO(totalIncome, totalExpense, totalIncome.subtract(totalExpense));
	}

	private Client getClient() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Client> client = clientRepository.findByEmail(email);
		return client.orElse(null);
	}

	private static String getDateFormatted(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
	}
}
