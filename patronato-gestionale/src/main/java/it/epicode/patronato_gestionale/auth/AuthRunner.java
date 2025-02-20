package it.epicode.patronato_gestionale.auth;

import it.epicode.patronato_gestionale.enums.Role;
import it.epicode.patronato_gestionale.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AuthRunner implements ApplicationRunner {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (appUserService.findByUsername("admin").isEmpty()) {
            appUserService.registerUser(
                    "admin",
                    "adminpwd",
                    "admin@example.com",
                    "Admin",
                    "Superuser",
                    Set.of(Role.ROLE_ADMIN)
            );
        }

        if (appUserService.findByUsername("collaborator").isEmpty()) {
            appUserService.registerUser(
                    "collaborator",
                    "collaboratorpwd",
                    "collaborator@example.com",
                    "Collaboratore",
                    "Aziendale",
                    Set.of(Role.ROLE_COLLABORATOR)
            );
        }
        // Aggiungiamo un utente di prova con ROLE_USER
        if (appUserService.findByUsername("user").isEmpty()) {
            appUserService.registerUser(
                    "user",
                    "userpwd",
                    "user@example.com",
                    "User",
                    "Test",
                    Set.of(Role.ROLE_USER)
            );
        }
    }
}