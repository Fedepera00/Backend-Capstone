package it.epicode.patronato_gestionale.auth;

import it.epicode.patronato_gestionale.dto.UpdateUserRequest;
import it.epicode.patronato_gestionale.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest registerRequest) {
        appUserService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                registerRequest.getNome(),
                registerRequest.getCognome(),
                Set.of(Role.ROLE_COLLABORATOR)
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registrazione avvenuta con successo");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = appUserService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(new AuthResponse(token));
    }
    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(
            @RequestBody UpdateUserRequest updateUserRequest,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Utente non autenticato");
        }

        String username = authentication.getName(); // Ottieni il nome utente dall'Authentication

        appUserService.updateUser(username, updateUserRequest);
        return ResponseEntity.ok("Profilo aggiornato con successo");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non autenticato");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userDetails);
    }
}