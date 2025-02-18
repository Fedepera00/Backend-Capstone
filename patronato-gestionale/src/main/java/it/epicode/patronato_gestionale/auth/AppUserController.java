package it.epicode.patronato_gestionale.auth;

import it.epicode.patronato_gestionale.dto.AppUserDTO;
import it.epicode.patronato_gestionale.dto.PageDTO;
import it.epicode.patronato_gestionale.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserService appUserService;


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppUser> getLoggedUser(Authentication authentication) {
        try {
            System.out.println("Authentication: " + authentication);
            String username = authentication.getName(); // Ottiene lo username dal token
            System.out.println("Username: " + username);
            AppUser user = appUserService.loadUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.err.println("Errore durante il recupero del profilo utente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Endpoint per creare un nuovo utente (solo Admin)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        try {
            AppUser nuovoUtente = appUserService.registerUser(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getNome(),
                    user.getCognome(),
                    user.getRoles()
            );
            return new ResponseEntity<>(nuovoUtente, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Logga l'errore per debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint per aggiornare i dettagli di un utente (solo Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUser> updateUser(
            @PathVariable Long id,
            @RequestBody AppUser updatedUser) {
        try {
            AppUser utenteAggiornato = appUserService.updateUserDetails(id, updatedUser);
            return ResponseEntity.ok(utenteAggiornato);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/{id}/ruolo")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUser> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        try {
            String nuovoRuolo = payload.get("ruolo");
            if (nuovoRuolo == null || (!nuovoRuolo.equals("ROLE_ADMIN")
                    && !nuovoRuolo.equals("ROLE_COLLABORATOR")
                    && !nuovoRuolo.equals("ROLE_USER"))) {
                throw new IllegalArgumentException("Ruolo non valido: " + nuovoRuolo);
            }

            Role ruoloEnum = Role.valueOf(nuovoRuolo);
            AppUser utenteAggiornato = appUserService.updateUserRole(id, ruoloEnum);
            return ResponseEntity.ok(utenteAggiornato);
        } catch (IllegalArgumentException e) {
            System.err.println("Ruolo non valido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (EntityNotFoundException e) {
            System.err.println("Utente non trovato: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiornamento del ruolo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Endpoint per aggiornare i dettagli dell'utente loggato
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppUser> updateLoggedUser(Authentication authentication, @RequestBody AppUser updatedUser) {
        try {
            String username = authentication.getName();

            AppUser existingUser = appUserService.loadUserByUsername(username);

            existingUser.setNome(updatedUser.getNome());
            existingUser.setCognome(updatedUser.getCognome());
            existingUser.setEmail(updatedUser.getEmail());


            AppUser savedUser = appUserRepository.save(existingUser);
            System.out.println("Dati salvati nel database: " + savedUser);

            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiornamento del profilo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping
    public ResponseEntity<PageDTO<AppUserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        Page<AppUser> utentiPage = appUserService.getAllUsers(PageRequest.of(page, size));

        List<AppUserDTO> utentiDTO = utentiPage.getContent().stream()
                .map(utente -> new AppUserDTO(
                        utente.getId(),
                        utente.getNome(),
                        utente.getCognome(),
                        utente.getEmail(),
                        utente.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
                ))
                .toList();

        PageDTO<AppUserDTO> response = new PageDTO<>(
                utentiDTO,
                utentiPage.getNumber(),
                utentiPage.getSize(),
                utentiPage.getTotalElements(),
                utentiPage.getTotalPages(),
                utentiPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            System.out.println("Eliminazione utente ID: " + id);
            appUserService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            System.err.println("Errore: Utente non trovato con ID " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}