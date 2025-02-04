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
    public ResponseEntity<Appuntamento> createAppuntamento(@RequestBody AppuntamentoRequest appuntamentoRequest,
                                                           @RequestHeader("Authorization") String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        Appuntamento appuntamento = appuntamentoService.createAppuntamento(
                appuntamentoRequest.getTitolo(),
                appuntamentoRequest.getDataOra(),
                appuntamentoRequest.getLuogo(),
                appuntamentoRequest.getDescrizione(),
                appuntamentoRequest.getNome(),
                appuntamentoRequest.getCognome(),
                appuntamentoRequest.getStato(),
                username
        );
        return ResponseEntity.ok(appuntamento);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping
    public ResponseEntity<List<Appuntamento>> getAllAppuntamenti(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String stato) {
        List<Appuntamento> appuntamenti = appuntamentoService.filterAppuntamenti(nome, cognome, stato);
        return ResponseEntity.ok(appuntamenti);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Appuntamento> updateAppuntamento(
            @PathVariable Long id,
            @RequestBody AppuntamentoRequest appuntamentoRequest) {
        Appuntamento appuntamento = appuntamentoService.updateAppuntamento(
                id,
                appuntamentoRequest.getTitolo(),
                appuntamentoRequest.getDataOra(),
                appuntamentoRequest.getLuogo(),
                appuntamentoRequest.getNome(),
                appuntamentoRequest.getCognome(),
                appuntamentoRequest.getStato()
        );
        return ResponseEntity.ok(appuntamento);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppuntamento(@PathVariable Long id) {
        appuntamentoService.deleteAppuntamento(id);
        return ResponseEntity.noContent().build();
    }
}