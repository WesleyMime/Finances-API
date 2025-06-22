package br.com.finances.api.client;

import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.IncomeRepository;
import br.com.finances.config.auth.SignForm;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public ClientService(ClientRepository repository, IncomeRepository incomeRepository,
                         ExpenseRepository expenseRepository) {
        this.repository = repository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
    }

    public Optional<ClientDTO> getOne(Principal principal) {
        Optional<Client> clientOptional = repository.findByEmail(principal.getName());
        return clientOptional.map(ClientDTO::new);
    }

    public Optional<ClientDTO> put(SignForm signForm, Principal principal) {
        Optional<Client> optional = repository.findByEmail(principal.getName());
        if (optional.isEmpty()) return Optional.empty();

        Client formClient = signForm.converter();
        Client client = optional.get();
        client.setName(formClient.getName());
        client.setEmail(formClient.getUsername());
        client.setPassword(formClient.getPassword());
        repository.save(client);
        return Optional.of(client).map(ClientDTO::new);
    }

    public Optional<ClientDTO> delete(Principal principal) {
        Optional<Client> clientOptional = repository.findByEmail(principal.getName());
        if (clientOptional.isEmpty()) return Optional.empty();

        Client client = clientOptional.get();
        repository.deleteById(client.getId());
        return clientOptional.map(ClientDTO::new);
    }
}
