package it.epicode.patronato_gestionale.auth;

import it.epicode.patronato_gestionale.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserService appUserService;

    // Endpoint per ottenere tutti gli utenti (accesso consentito ad Admin e Collaboratori)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = appUserRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Endpoint per creare un nuovo utente (solo Admin)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        System.out.println(user);
            AppUser nuovoUtente = appUserService.registerUser(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getNome(),
                    user.getCognome(),
                    user.getRoles()
            );
            return new ResponseEntity<>(nuovoUtente, HttpStatus.CREATED);


    }

    // Endpoint per aggiornare il ruolo di un utente (solo Admin)
    @PutMapping("/{id}/ruolo")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUser> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        try {
            // Verifica che il ruolo sia presente nel payload
            String nuovoRuolo = payload.get("ruolo");
            if (!nuovoRuolo.equals("ROLE_ADMIN") && !nuovoRuolo.equals("ROLE_COLLABORATOR")) {
                throw new IllegalArgumentException("Ruolo non valido");
            }
            System.out.println("Payload ricevuto: " + payload);
            Role ruoloEnum = Role.valueOf(nuovoRuolo);
            AppUser utenteAggiornato = appUserService.updateUserRole(id, ruoloEnum);
            return ResponseEntity.ok(utenteAggiornato);
        } catch (IllegalArgumentException e) {
            System.err.println("Ruolo non valido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Endpoint per eliminare un utente (solo Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            appUserService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}