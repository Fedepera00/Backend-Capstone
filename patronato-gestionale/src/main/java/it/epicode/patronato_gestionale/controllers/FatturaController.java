package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.FatturaRequest;
import it.epicode.patronato_gestionale.dto.PageDTO;
import it.epicode.patronato_gestionale.entities.Fattura;
import it.epicode.patronato_gestionale.enums.FatturaStato;
import it.epicode.patronato_gestionale.services.FatturaService;
import it.epicode.patronato_gestionale.services.PdfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fatture")
public class FatturaController {

    @Autowired
    private FatturaService fatturaService;

    @Autowired
    private PdfService pdfService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<List<Fattura>> getAllFatture() {
        return ResponseEntity.ok(fatturaService.getAllFatture());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Fattura> getFatturaById(@PathVariable Long id) {
        return ResponseEntity.ok(fatturaService.getFatturaById(id));
    }

    @GetMapping("/paginate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<PageDTO<Fattura>> getFatturePaginate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Fattura> fatturePage = fatturaService.getFatturePaginate(page, size);

        PageDTO<Fattura> pageDTO = new PageDTO<>(
                fatturePage.getContent(),
                fatturePage.getNumber(),
                fatturePage.getSize(),
                fatturePage.getTotalElements(),
                fatturePage.getTotalPages(),
                fatturePage.isLast()
        );

        return ResponseEntity.ok(pageDTO);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Fattura> createFattura(@Valid @RequestBody FatturaRequest request) {
        return ResponseEntity.ok(fatturaService.createFattura(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Fattura> updateFattura(@PathVariable Long id, @Valid @RequestBody FatturaRequest request) {
        return ResponseEntity.ok(fatturaService.updateFattura(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<Void> deleteFattura(@PathVariable Long id) {
        fatturaService.deleteFattura(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COLLABORATOR')")
    public ResponseEntity<byte[]> downloadFatturaPdf(@PathVariable Long id) {
        Fattura fattura = fatturaService.getFatturaById(id);
        byte[] pdfContent = pdfService.generateFatturaPdf(fattura);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fattura_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}