package br.com.finances.api.client;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<ClientDTO> getClient(Principal principal) {
        Optional<ClientDTO> clientById = clientService.getOne(principal);
        return clientById.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping
    public ResponseEntity<ClientDTO> patchClient(@Valid @RequestBody ClientForm clientForm,
                                               Principal principal) {
        Optional<ClientDTO> clientById = clientService.patchClient(clientForm, principal);
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
