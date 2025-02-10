package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.PraticaRequest;
import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.services.PraticaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<?> createPratica(@Valid @RequestBody PraticaRequest praticaRequest) {
        try {
            Pratica nuovaPratica = praticaService.createPratica(
                    praticaRequest.getTitolo(),
                    praticaRequest.getDescrizione(),
                    praticaRequest.getRichiedente(),
                    praticaRequest.getCodiceFiscale(),
                    praticaRequest.getCategoria(),
                    praticaRequest.getNote(),
                    StatoPratica.valueOf(praticaRequest.getStato()) // Converti lo stato in enum
            );
            return ResponseEntity.ok(nuovaPratica);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Stato non valido: " + praticaRequest.getStato());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping("/all")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping
    public ResponseEntity<Page<Pratica>> getPratiche(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Page<Pratica> pratiche = praticaService.getPratichePaginate(page, size);
        return ResponseEntity.ok(pratiche);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Pratica> updatePratica(@PathVariable Long id, @RequestBody PraticaRequest request) {
        Pratica pratica = praticaService.updatePratica(
                id,
                request.getTitolo(),
                request.getDescrizione(),
                request.getRichiedente(),
                request.getCategoria(),
                request.getNote(),
                request.getCodiceFiscale(),
                StatoPratica.valueOf(request.getStato())
        );
        return ResponseEntity.ok(pratica);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePratica(@PathVariable Long id) {
        praticaService.deletePratica(id);
        return ResponseEntity.noContent().build();
    }
}