package br.com.finances.unit.api.service;

import br.com.finances.api.client.*;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
                expenseRepository, new BCryptPasswordEncoder());
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
    void patchesClientSuccessfully() {
        ClientForm clientForm = new ClientForm(
                "newName", "newEmail@email.com", "newPassword");

        Optional<ClientDTO> optional = clientService.patchClient(clientForm, PRINCIPAL);
        assertTrue(optional.isPresent());
        ClientDTO client = optional.get();
        assertEquals(clientForm.name(), client.getName());
        assertEquals(clientForm.email(), client.getEmail());
    }

    @Test
    void patchesClientSuccessfullyWithOnlyEmail() {
        ClientForm clientForm = new ClientForm(
                null, "newEmail@email.com", null);

        Optional<ClientDTO> optional = clientService.patchClient(clientForm, PRINCIPAL);
        assertTrue(optional.isPresent());
        ClientDTO client = optional.get();
        assertEquals(MOCK_CLIENT.getName(), client.getName());
        assertEquals(clientForm.email(), client.getEmail());
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