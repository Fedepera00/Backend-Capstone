package it.epicode.patronato_gestionale.auth;

import it.epicode.patronato_gestionale.enums.Role;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public AppUser registerUser(String username, String password, String email, String nome, String cognome, Set<Role> roles) {
        if (appUserRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setEmail(email);
        appUser.setNome(nome);
        appUser.setCognome(cognome);
        appUser.setRoles(roles); // Assicurati che roles non sia null

        return appUserRepository.save(appUser);
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public String authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            throw new SecurityException("Credenziali non valide", e);
        }
    }

    public AppUser loadUserByUsername(String username) {
        System.out.println("Caricamento utente con username: " + username);
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));
    }
    public AppUser updateUserRole(Long id, Role nuovoRuolo) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con ID: " + id));
        appUser.getRoles().clear(); // Rimuovi i vecchi ruoli
        appUser.getRoles().add(nuovoRuolo); // Assegna il nuovo ruolo
        return appUserRepository.save(appUser);
    }

    public void deleteUser(Long id) {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("Utente non trovato con ID: " + id);
        }
        appUserRepository.deleteById(id);
    }
    public AppUser updateUserDetails(Long id, AppUser updatedUser) {
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con ID: " + id));

        existingUser.setNome(updatedUser.getNome());
        existingUser.setCognome(updatedUser.getCognome());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return appUserRepository.save(existingUser);
    }
    public AppUser updateUserDetailsByUsername(String username, AppUser updatedUser) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));

        appUser.setNome(updatedUser.getNome());
        appUser.setCognome(updatedUser.getCognome());
        appUser.setEmail(updatedUser.getEmail());

        // Solo se la password è fornita, la aggiorniamo
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            appUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return appUserRepository.save(appUser);
    }
}