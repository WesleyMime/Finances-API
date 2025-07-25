package br.com.finances.api.client;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<ClientDTO> getOne(Principal principal) {
        Optional<Client> clientOptional = repository.findByEmail(principal.getName());
        return clientOptional.map(ClientDTO::new);
    }

    public Optional<ClientDTO> patchClient(ClientForm clientForm, Principal principal) {
        Optional<Client> optional = repository.findByEmail(principal.getName());
        if (optional.isEmpty()) return Optional.empty();

        Client client = optional.get();
        client.setName(clientForm.name());
        client.setEmail(clientForm.email());
        if (clientForm.password() != null)
            client.setPassword(passwordEncoder.encode(clientForm.password()));
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
