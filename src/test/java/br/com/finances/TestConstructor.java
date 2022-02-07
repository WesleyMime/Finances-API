package br.com.finances;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.finances.dto.ExpenseDTO;
import br.com.finances.dto.IncomeDTO;
import br.com.finances.form.ExpenseForm;
import br.com.finances.form.IncomeForm;
import br.com.finances.form.LoginForm;
import br.com.finances.form.SignForm;
import br.com.finances.model.Category;
import br.com.finances.model.Client;
import br.com.finances.model.Expense;
import br.com.finances.model.Income;

public class TestConstructor {

	private List<Client> listClient = generateClient();
	private List<Income> listIncome;
	private List<Expense> listExpense;
	private Client client = listClient.get(0);
	
	public void setClient() {
		Client client = listClient.get(0);
		UsernamePasswordAuthenticationToken authentication = 
				new UsernamePasswordAuthenticationToken(client, null, client.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	private List<Client> generateClient() {
		List<Client> listClient = new ArrayList<>();
		listClient.add(new Client("test", "test@email.com", "test"));
		return listClient;
	}
	
	public List<Income> generateIncome() {
		List<Income> listIncome = new ArrayList<>();
		listIncome.add(new Income("description income test", new BigDecimal("1500"), LocalDate.now(), client));
		listIncome.add(new Income("description test income", new BigDecimal("2500"), LocalDate.now(), client));
		listIncome.add(new Income("test income description", new BigDecimal("3500"), LocalDate.now(), client));
		return listIncome;
	}
		
	public List<Expense> generateExpense() {
		List<Expense> listExpense = new ArrayList<>();
		listExpense.add(new Expense("description expense test", new BigDecimal("1500"), LocalDate.now(), Category.Food, client));
		listExpense.add(new Expense("description test expense", new BigDecimal("2500"), LocalDate.now(), Category.Health, client));
		listExpense.add(new Expense("test expense description", new BigDecimal("3500"), LocalDate.now(), null, client));
		return listExpense;
	}	
	
	public List<IncomeForm> generateIncomeForm() {
		List<IncomeForm> listIncomeForm = new ArrayList<>();
		listIncomeForm.add(new IncomeForm("Income description", new BigDecimal("1500"), LocalDate.of(2022, 01, 01), client));
		listIncomeForm.add(new IncomeForm("Description income", new BigDecimal("3000"), LocalDate.of(2022, 02, 01), client));
		listIncomeForm.add(new IncomeForm("Income description", new BigDecimal("1000"), LocalDate.of(2022, 01, 25), client));
		listIncomeForm.add(new IncomeForm("Description income", new BigDecimal("2000"), LocalDate.of(2022, 03, 25), client));
		return listIncomeForm;
	}
	
	public List<ExpenseForm> generateExpenseForm() {
		Client client = listClient.get(0);
		List<ExpenseForm> listExpenseForm = new ArrayList<>();
		listExpenseForm.add(new ExpenseForm("Expense", new BigDecimal("1500"), LocalDate.of(2022, 01, 01), Category.Home, client));
		listExpenseForm.add(new ExpenseForm("Description expense", new BigDecimal("3000"), LocalDate.of(2022, 02, 01), Category.Others, client));
		listExpenseForm.add(new ExpenseForm("Expense", new BigDecimal("1000"), LocalDate.of(2022, 01, 25), Category.Unforeseen, client));
		listExpenseForm.add(new ExpenseForm("Description expense 2", new BigDecimal("2000"), LocalDate.of(2022, 03, 25), null, client));
		return listExpenseForm;
	}
	
	public List<IncomeDTO> generateIncomeDto() {
		this.listIncome = generateIncome();
		
		List<IncomeDTO> listIncomeDto = new ArrayList<>();
		
		listIncome.forEach(i -> {
			listIncomeDto.add(new IncomeDTO(i));
		});
		return listIncomeDto;
	}
	
	public List<ExpenseDTO> generateExpenseDto() {
		this.listExpense = generateExpense();
		
		List<ExpenseDTO> listExpenseDto = new ArrayList<>();
		
		listExpense.forEach(e -> {
			listExpenseDto.add(new ExpenseDTO(e));
		});
		return listExpenseDto;		
	}

	public List<Client> getListClient() {
		return listClient;
	}

	public List<SignForm> generateSignForm() {
		List<SignForm> listSignInForm = new ArrayList<>();
		listSignInForm.add(new SignForm("Test", "test@email.com", "test"));
		listSignInForm.add(new SignForm("", "test@email.com", "test"));
		listSignInForm.add(new SignForm("Test", "test", "test"));
		listSignInForm.add(new SignForm("Test", "", "test"));
		listSignInForm.add(new SignForm("Test", "test@email.com", ""));
		listSignInForm.add(new SignForm("Before", "beforeeach@email.com", "each"));
		return listSignInForm;
	}

	public List<LoginForm> generateLoginForm() {
		List<LoginForm> listLoginForm = new ArrayList<>();
		listLoginForm.add(new LoginForm("test@email.com", "test"));
		listLoginForm.add(new LoginForm("beforeeach@email.com", "each"));
		listLoginForm.add(new LoginForm("test", "test"));
		listLoginForm.add(new LoginForm("", "test"));
		listLoginForm.add(new LoginForm("test", ""));
		listLoginForm.add(new LoginForm("testtttttttttt@email.com", "each"));
		return listLoginForm;
	}
	
}
