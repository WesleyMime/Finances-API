package br.com.finances.unit.api.service;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientDTO;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.client.ClientService;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.IncomeRepository;
import br.com.finances.config.auth.SignForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ClientServiceTest {

    private final ClientService clientService;
    private final ClientRepository clientRepository;

    public ClientServiceTest() {
        clientRepository = Mockito.mock(ClientRepository.class);
        IncomeRepository incomeRepository = Mockito.mock(IncomeRepository.class);
        ExpenseRepository expenseRepository = Mockito.mock(ExpenseRepository.class);
        this.clientService = new ClientService(clientRepository, incomeRepository,
                expenseRepository);
    }

    private static final Client MOCK_CLIENT = new Client("John Doe", "test@email.com", "password");
    private static final Principal PRINCIPAL = () -> "test@email.com";

    @BeforeEach
    void setUp() {
        when(clientRepository.findByEmail(PRINCIPAL.getName())).thenReturn(
                Optional.of(MOCK_CLIENT));
    }

    @Test
    void returnsClientSuccessfully() {
        Optional<ClientDTO> optional = clientService.getOne(PRINCIPAL);

        assertTrue(optional.isPresent());
        ClientDTO client = optional.get();
        assertEquals(MOCK_CLIENT.getName(), client.getName());
        assertEquals(MOCK_CLIENT.getUsername(), client.getEmail());
    }

    @Test
    void updatesClientSuccessfully() {
        SignForm signForm = new SignForm(
                "newName", "newEmail@email.com", "newPassword");

        Optional<ClientDTO> optional = clientService.put(signForm, PRINCIPAL);
        assertTrue(optional.isPresent());
        ClientDTO client = optional.get();
        assertEquals(signForm.getName(), client.getName());
        assertEquals(signForm.getEmail(), client.getEmail());
    }

    @Test
    void deletesClientSuccessfully() {
        Optional<ClientDTO> optional = clientService.delete(PRINCIPAL);
        assertTrue(optional.isPresent());
        ClientDTO client = optional.get();
        assertEquals(MOCK_CLIENT.getName(), client.getName());
        assertEquals(MOCK_CLIENT.getUsername(), client.getEmail());
    }
}