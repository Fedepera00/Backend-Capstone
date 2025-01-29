package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.PraticaRequest;
import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.services.PraticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pratiche")
public class PraticaController {

    @Autowired
    private PraticaService praticaService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @PostMapping
    public ResponseEntity<Pratica> createPratica(@RequestBody PraticaRequest praticaRequest) {
        Pratica pratica = praticaService.createPratica(
                praticaRequest.getTitolo(),
                praticaRequest.getDescrizione(),
                praticaRequest.getRichiedente(),
                praticaRequest.getCategoria(),
                praticaRequest.getNote()
        );
        return ResponseEntity.ok(pratica);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping
    public ResponseEntity<List<Pratica>> getAllPratiche() {
        List<Pratica> pratiche = praticaService.getAllPratiche();
        return ResponseEntity.ok(pratiche);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Pratica> getPraticaById(@PathVariable Long id) {
        Pratica pratica = praticaService.getPraticaById(id);
        return ResponseEntity.ok(pratica);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')") // Solo admin può aggiornare
    @PutMapping("/{id}")
    public ResponseEntity<Pratica> updatePratica(@PathVariable Long id, @RequestParam String titolo,
                                                 @RequestParam String descrizione, @RequestParam StatoPratica stato) {
        Pratica pratica = praticaService.updatePratica(id, titolo, descrizione, stato);
        return ResponseEntity.ok(pratica);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')") // Solo admin può eliminare
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePratica(@PathVariable Long id) {
        praticaService.deletePratica(id);
        return ResponseEntity.noContent().build();
    }
}