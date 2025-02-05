package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.FatturaRequest;
import it.epicode.patronato_gestionale.entities.Fattura;
import it.epicode.patronato_gestionale.services.FatturaService;
import it.epicode.patronato_gestionale.services.PdfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fatture")
public class FatturaController {

    @Autowired
    private FatturaService fatturaService;

    @Autowired
    private PdfService pdfService;

    // ✅ GET ALL - Ottieni tutte le fatture**
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<List<Fattura>> getAllFatture() {
        return ResponseEntity.ok(fatturaService.getAllFatture());
    }

    // ✅ GET BY ID - Ottieni una singola fattura**
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Fattura> getFatturaById(@PathVariable Long id) {
        return ResponseEntity.ok(fatturaService.getFatturaById(id));
    }

    // ✅ CREATE - Crea una nuova fattura**
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Fattura> createFattura(@Valid @RequestBody FatturaRequest request) {
        return ResponseEntity.ok(fatturaService.createFattura(request));
    }

    // ✅ UPDATE - Modifica una fattura esistente**
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Fattura> updateFattura(@PathVariable Long id, @Valid @RequestBody FatturaRequest request) {
        return ResponseEntity.ok(fatturaService.updateFattura(id, request));
    }

    // ✅ DELETE - Elimina una fattura**
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Void> deleteFattura(@PathVariable Long id) {
        fatturaService.deleteFattura(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ PDF DOWNLOAD - Scarica la fattura in PDF**
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<byte[]> downloadFatturaPdf(@PathVariable Long id) {
        Fattura fattura = fatturaService.getFatturaById(id);

        if (fattura == null) {
            throw new RuntimeException("Fattura non trovata con ID: " + id);
        }

        byte[] pdfContent = pdfService.generateFatturaPdf(fattura);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fattura_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}