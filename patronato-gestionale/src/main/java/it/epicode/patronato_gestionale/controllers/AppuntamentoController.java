package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.AppuntamentoRequest;
import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.services.AppuntamentoService;
import it.epicode.patronato_gestionale.auth.JwtTokenUtil; // Importa JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appuntamenti")
public class AppuntamentoController {

    @Autowired
    private AppuntamentoService appuntamentoService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil; // Inietta il JwtTokenUtil

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @PostMapping
    public ResponseEntity<Appuntamento> createAppuntamento(@RequestBody AppuntamentoRequest appuntamentoRequest,
                                                           @RequestHeader("Authorization") String token) {
        // Estrae lo username dal token
        String username = jwtTokenUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // Controllo sul campo titolo
        if (appuntamentoRequest.getTitolo() == null || appuntamentoRequest.getTitolo().isEmpty()) {
            return ResponseEntity.badRequest().build(); // Errore 400 se il titolo è nullo o vuoto
        }

        // Creazione dell'appuntamento
        Appuntamento appuntamento = appuntamentoService.createAppuntamento(
                appuntamentoRequest.getTitolo(),
                appuntamentoRequest.getDataOra(),
                appuntamentoRequest.getLuogo(),
                appuntamentoRequest.getDescrizione(),
                username
        );
        return ResponseEntity.ok(appuntamento);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping
    public ResponseEntity<List<Appuntamento>> getAllAppuntamenti() {
        List<Appuntamento> appuntamenti = appuntamentoService.getAllAppuntamenti();
        return ResponseEntity.ok(appuntamenti);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Appuntamento> updateAppuntamento(@PathVariable Long id,
                                                           @RequestParam String titolo,
                                                           @RequestParam LocalDateTime dataOra,
                                                           @RequestParam String luogo) {
        Appuntamento appuntamento = appuntamentoService.updateAppuntamento(id, titolo, dataOra, luogo);
        return ResponseEntity.ok(appuntamento);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppuntamento(@PathVariable Long id) {
        appuntamentoService.deleteAppuntamento(id);
        return ResponseEntity.noContent().build();
    }
}