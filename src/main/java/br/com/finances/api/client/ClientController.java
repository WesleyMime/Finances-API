package br.com.finances.api.client;

import br.com.finances.config.auth.SignForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<ClientDTO> getClient(Principal principal) {
        Optional<ClientDTO> clientById = clientService.getOne(principal);
        return clientById.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<ClientDTO> putClient(@Valid @RequestBody SignForm signForm,
                                               Principal principal) {
        Optional<ClientDTO> clientById = clientService.put(signForm, principal);
        return clientById.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<ClientDTO> deleteClient(Principal principal) {
        Optional<ClientDTO> clientById = clientService.delete(principal);
        if (clientById.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
