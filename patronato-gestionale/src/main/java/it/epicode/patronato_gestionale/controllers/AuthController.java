package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.auth.AuthResponse;
import it.epicode.patronato_gestionale.auth.LoginRequest;
import it.epicode.patronato_gestionale.auth.RegisterRequest;
import it.epicode.patronato_gestionale.dto.UpdateUserRequest;
import it.epicode.patronato_gestionale.entities.AppUser;
import it.epicode.patronato_gestionale.enums.Role;
import it.epicode.patronato_gestionale.services.AppUserService;
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
                Set.of(Role.ROLE_USER)
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
    public ResponseEntity<AppUser> updateProfile(
            @RequestBody UpdateUserRequest updateUserRequest,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = authentication.getName();
        appUserService.updateUser(username, updateUserRequest);
        AppUser updatedUser = appUserService.loadUserByUsername(username);

        return ResponseEntity.ok(updatedUser);
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