package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.AppuntamentoRequest;
import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.services.AppuntamentoService;
import it.epicode.patronato_gestionale.auth.JwtTokenUtil;
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
    private JwtTokenUtil jwtTokenUtil;


    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @PostMapping
    public ResponseEntity<Appuntamento> createAppuntamento(
            @RequestBody AppuntamentoRequest appuntamentoRequest,
            @RequestHeader("Authorization") String token) {
        System.out.println("Richiesta di creazione appuntamento ricevuta.");
        System.out.println("Token ricevuto: " + token);

        String username = jwtTokenUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        System.out.println("Username estratto dal token: " + username);

        Appuntamento appuntamento = appuntamentoService.createAppuntamento(
                appuntamentoRequest.getTitolo(),
                appuntamentoRequest.getDataOra(),
                appuntamentoRequest.getLuogo(),
                appuntamentoRequest.getDescrizione(),
                appuntamentoRequest.getNome(),
                appuntamentoRequest.getCognome(),
                appuntamentoRequest.getStato(),
                appuntamentoRequest.getEmail(),
                username
        );

        System.out.println("Appuntamento creato con successo: " + appuntamento);
        return ResponseEntity.ok(appuntamento);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping
    public ResponseEntity<List<Appuntamento>> getAllAppuntamenti(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String stato,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        System.out.println("Richiesta per ottenere tutti gli appuntamenti.");
        System.out.println("Filtri applicati - Nome: " + nome + ", Cognome: " + cognome + ", Stato: " + stato);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // Converte le stringhe in LocalDateTime, se presenti
        if (startDate != null && endDate != null) {
            startDateTime = LocalDateTime.parse(startDate);
            endDateTime = LocalDateTime.parse(endDate);
            System.out.println("Filtri aggiuntivi - Data inizio: " + startDateTime + ", Data fine: " + endDateTime);
        }

        List<Appuntamento> appuntamenti = appuntamentoService.filterAppuntamenti(nome, cognome, stato, startDateTime, endDateTime);
        System.out.println("Numero di appuntamenti trovati: " + appuntamenti.size());

        return ResponseEntity.ok(appuntamenti);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Appuntamento> updateAppuntamento(
            @PathVariable Long id,
            @RequestBody AppuntamentoRequest appuntamentoRequest) {
        System.out.println("Richiesta di aggiornamento appuntamento con ID: " + id);

        Appuntamento appuntamento = appuntamentoService.updateAppuntamento(
                id,
                appuntamentoRequest.getTitolo(),
                appuntamentoRequest.getDataOra(),
                appuntamentoRequest.getLuogo(),
                appuntamentoRequest.getNome(),
                appuntamentoRequest.getCognome(),
                appuntamentoRequest.getStato(),
                appuntamentoRequest.getEmail()
        );

        System.out.println("Appuntamento aggiornato: " + appuntamento);
        return ResponseEntity.ok(appuntamento);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppuntamento(@PathVariable Long id) {
        System.out.println("Richiesta di eliminazione appuntamento con ID: " + id);

        appuntamentoService.deleteAppuntamento(id);
        System.out.println("Appuntamento con ID " + id + " eliminato con successo.");

        return ResponseEntity.noContent().build();
    }
}