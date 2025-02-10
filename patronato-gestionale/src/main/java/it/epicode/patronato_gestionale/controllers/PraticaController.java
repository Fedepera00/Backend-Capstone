package it.epicode.patronato_gestionale.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import it.epicode.patronato_gestionale.dto.FileUploadSchema;
import it.epicode.patronato_gestionale.dto.PageDTO;
import it.epicode.patronato_gestionale.dto.PraticaRequest;
import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.services.PraticaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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
                    StatoPratica.valueOf(praticaRequest.getStato())
            );
            return ResponseEntity.ok(nuovaPratica);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Stato non valido: " + praticaRequest.getStato());
        }
    }

    @GetMapping("/uploads/pdf/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Path filePath = Paths.get("uploads/pdf").resolve(fileName).normalize();
        System.out.println("Tentativo di accedere al file: " + filePath);

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                System.out.println("Errore: File non trovato o non leggibile: " + filePath);
                throw new FileNotFoundException("File non trovato o non leggibile: " + fileName);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            System.out.println("Errore durante il download del file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{id}/upload-pdf")
    @Operation(
            summary = "Carica un file PDF associato a una pratica",
            description = "Endpoint per caricare un file PDF associato a una pratica esistente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = FileUploadSchema.class)
                    )
            )
    )
    public ResponseEntity<String> uploadPdf(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String relativePath = praticaService.uploadPdf(id, file);
            String fileUrl = "/uploads/pdf/" + relativePath;
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore durante il caricamento del file: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/download-pdf")
    @Operation(summary = "Scarica il PDF associato a una pratica")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        try {
            return praticaService.downloadPdf(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
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
    public ResponseEntity<PageDTO<Pratica>> getPratiche(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        Page<Pratica> praticaPage = praticaService.getPratichePaginate(page, size);
        PageDTO<Pratica> pageDto = new PageDTO<>(
                praticaPage.getContent(),
                praticaPage.getNumber(),
                praticaPage.getSize(),
                praticaPage.getTotalElements(),
                praticaPage.getTotalPages(),
                praticaPage.isLast()
        );
        return ResponseEntity.ok(pageDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COLLABORATOR')")
    @GetMapping("/search")
    public ResponseEntity<List<Pratica>> searchPratiche(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String requester,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Pratica> pratiche = praticaService.searchPratiche(title, requester, status, date);
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