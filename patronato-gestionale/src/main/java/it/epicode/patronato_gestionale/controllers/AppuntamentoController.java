package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.AppuntamentoRequest;
import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.services.AppuntamentoService;
import it.epicode.patronato_gestionale.auth.JwtTokenUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appuntamenti")
public class AppuntamentoController {

    @Autowired
    private AppuntamentoService appuntamentoService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Crea un nuovo appuntamento (accesso per ADMIN, COLLABORATOR e USER)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR') or hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<?> createAppuntamento(
            @RequestBody AppuntamentoRequest appuntamentoRequest,
            @RequestHeader("Authorization") String token) {
        try {
            String username = jwtTokenUtil.getUsernameFromToken(token.replace("Bearer ", ""));
            Appuntamento appuntamento = appuntamentoService.createAppuntamento(
                    appuntamentoRequest.getTitolo(),
                    appuntamentoRequest.getDataOra(),
                    appuntamentoRequest.getLuogo(),
                    appuntamentoRequest.getDescrizione(),
                    appuntamentoRequest.getNome(),
                    appuntamentoRequest.getCognome(),
                    appuntamentoRequest.getStato(),
                    appuntamentoRequest.getEmail(),
                    username,
                    appuntamentoRequest.getTelefono()
            );
            return ResponseEntity.ok(appuntamento);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la creazione dell'appuntamento.");
        }
    }

    // Endpoint per ottenere gli orari disponibili per una data
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR') or hasRole('ROLE_USER')")
    @GetMapping("/availableSlots")
    public ResponseEntity<List<String>> getAvailableSlots(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<String> formattedSlots = appuntamentoService.getAvailableSlots(localDate)
                .stream()
                .map(time -> time.toString())
                .collect(Collectors.toList());
        return ResponseEntity.ok(formattedSlots);
    }

    // Endpoint per ottenere tutti gli appuntamenti con filtri opzionali
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR') or hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<Appuntamento>> getAllAppuntamenti(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String stato,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if (startDate != null && endDate != null) {
            startDateTime = LocalDateTime.parse(startDate);
            endDateTime = LocalDateTime.parse(endDate);
        }
        List<Appuntamento> appuntamenti = appuntamentoService.filterAppuntamenti(
                nome, cognome, stato, startDateTime, endDateTime);
        return ResponseEntity.ok(appuntamenti);
    }

    // Aggiorna un appuntamento (ADMIN e COLLABORATOR)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Appuntamento> updateAppuntamento(
            @PathVariable Long id,
            @RequestBody AppuntamentoRequest appuntamentoRequest) {
        Appuntamento appuntamento = appuntamentoService.updateAppuntamento(
                id,
                appuntamentoRequest.getTitolo(),
                appuntamentoRequest.getDataOra(),
                appuntamentoRequest.getLuogo(),
                appuntamentoRequest.getDescrizione(),
                appuntamentoRequest.getNome(),
                appuntamentoRequest.getCognome(),
                appuntamentoRequest.getStato(),
                appuntamentoRequest.getEmail(),
                appuntamentoRequest.getTelefono()
        );
        return ResponseEntity.ok(appuntamento);
    }

    // Elimina un appuntamento (solo ADMIN)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppuntamento(@PathVariable Long id) {
        appuntamentoService.deleteAppuntamento(id);
        return ResponseEntity.noContent().build();
    }
}