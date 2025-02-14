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
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appuntamenti")
public class AppuntamentoController {

    @Autowired
    private AppuntamentoService appuntamentoService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Consente l'accesso a ROLE_ADMIN, ROLE_COLLABORATOR e ROLE_USER
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
                    username
            );
            return ResponseEntity.ok(appuntamento);
        } catch (IllegalStateException ex) {
            // Ritorna un errore 409 Conflict se l'orario non è disponibile
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la creazione dell'appuntamento.");
        }
    }

    /**
     * Endpoint per ottenere gli orari disponibili per una data.
     * La data va passata come parametro di query nel formato "yyyy-MM-dd".
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR') or hasRole('ROLE_USER')")
    @GetMapping("/availableSlots")
    public ResponseEntity<List<String>> getAvailableSlots(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<LocalTime> availableSlots = appuntamentoService.getAvailableSlots(localDate);
        // Formatto gli orari come stringhe (es. "09:00", "09:15", etc.)
        List<String> formattedSlots = availableSlots.stream()
                .map(time -> time.toString())
                .collect(Collectors.toList());
        return ResponseEntity.ok(formattedSlots);
    }

    // Consente l'accesso a ROLE_ADMIN, ROLE_COLLABORATOR e ROLE_USER
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR') or hasRole('ROLE_USER')")
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

        // Se presenti, converte le stringhe in LocalDateTime
        if (startDate != null && endDate != null) {
            startDateTime = LocalDateTime.parse(startDate);
            endDateTime = LocalDateTime.parse(endDate);
            System.out.println("Filtri aggiuntivi - Data inizio: " + startDateTime + ", Data fine: " + endDateTime);
        }

        List<Appuntamento> appuntamenti = appuntamentoService.filterAppuntamenti(
                nome, cognome, stato, startDateTime, endDateTime);
        System.out.println("Numero di appuntamenti trovati: " + appuntamenti.size());

        return ResponseEntity.ok(appuntamenti);
    }

    // Riservato a ROLE_ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
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

    // Riservato a ROLE_ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppuntamento(@PathVariable Long id) {
        System.out.println("Richiesta di eliminazione appuntamento con ID: " + id);

        appuntamentoService.deleteAppuntamento(id);
        System.out.println("Appuntamento con ID " + id + " eliminato con successo.");

        return ResponseEntity.noContent().build();
    }
}