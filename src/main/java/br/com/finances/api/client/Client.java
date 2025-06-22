package br.com.finances.api.client;

import br.com.finances.api.expense.Expense;
import br.com.finances.api.income.Income;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Client implements UserDetails{

	@Serial
	private static final long serialVersionUID = 7569357973771317490L;

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String email;
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	private List<Authority> authorities = new ArrayList<>();

	@OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
	private List<Income> incomeList;

	@OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
	private List<Expense> expenseList;

	public Client() {
	}

	public Client(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Income> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List<Income> incomeList) {
		this.incomeList = incomeList;
	}

	public List<Expense> getExpenseList() {
		return expenseList;
	}

	public void setExpenseList(List<Expense> expenseList) {
		this.expenseList = expenseList;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "{"
				+ "\"name\":\""+ this.name + "\","
				+ "\"email\":\""+ this.email + "\","
				+ "\"password\":\""+ this.password + "\""
				+ "}";
	}
  
}
