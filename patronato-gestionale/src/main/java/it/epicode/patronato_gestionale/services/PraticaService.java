package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.repositories.PraticaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PraticaService {

    @Autowired
    private PraticaRepository praticaRepository;

    public Page<Pratica> getPratichePaginate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return praticaRepository.findAll(pageable);
    }

    public Pratica createPratica(String titolo, String descrizione, String richiedente, String codiceFiscale,
                                 String categoria, String note, StatoPratica stato) {
        Pratica pratica = new Pratica();
        pratica.setTitolo(titolo);
        pratica.setDescrizione(descrizione);
        pratica.setRichiedente(richiedente);
        pratica.setCodiceFiscale(codiceFiscale);
        pratica.setCategoria(categoria);
        pratica.setNote(note);
        pratica.setStato(stato);
        pratica.setDataCreazione(LocalDate.now());
        pratica.setUltimaModifica(LocalDateTime.now());
        return praticaRepository.save(pratica);
    }

    public List<Pratica> getAllPratiche() {
        return praticaRepository.findAll();
    }

    public Pratica getPraticaById(Long id) {
        return praticaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pratica non trovata con ID: " + id));
    }

    public Pratica updatePratica(Long id, String titolo, String descrizione, String richiedente,
                                 String categoria, String note, String codiceFiscale, StatoPratica stato) {
        Pratica pratica = getPraticaById(id);

        if (titolo != null) pratica.setTitolo(titolo);
        if (descrizione != null) pratica.setDescrizione(descrizione);
        if (richiedente != null) pratica.setRichiedente(richiedente);
        if (categoria != null) pratica.setCategoria(categoria);
        if (note != null) pratica.setNote(note);
        if (codiceFiscale != null) pratica.setCodiceFiscale(codiceFiscale);
        if (stato != null) pratica.setStato(stato);

        pratica.setUltimaModifica(LocalDateTime.now());
        return praticaRepository.save(pratica);
    }

    public void deletePratica(Long id) {
        praticaRepository.deleteById(id);
    }

    public List<Pratica> searchPratiche(String title, String requester, String status, LocalDate date) {
        return praticaRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("titolo")), "%" + title.toLowerCase() + "%"));
            }
            if (requester != null && !requester.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("richiedente")), "%" + requester.toLowerCase() + "%"));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("stato"), StatoPratica.valueOf(status)));
            }
            if (date != null) {
                predicates.add(criteriaBuilder.equal(root.get("dataCreazione"), date));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
    public void uploadPdf(Long id, MultipartFile file) throws Exception {
        Pratica pratica = getPraticaById(id);

        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Il file deve essere un PDF valido");
        }

        // Salva il file in una directory locale
        String directory = "uploads/pdf/";
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String filePath = directory + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // Associa il percorso del file alla pratica
        pratica.setPdfUrl(filePath);
        praticaRepository.save(pratica);
    }

    // Metodo per il download dei PDF
    public ResponseEntity<Resource> downloadPdf(Long id) throws Exception {
        Pratica pratica = getPraticaById(id);

        if (pratica.getPdfUrl() == null) {
            throw new IllegalArgumentException("Nessun file PDF associato a questa pratica");
        }

        Path filePath = Paths.get(pratica.getPdfUrl());
        org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new IllegalArgumentException("File non trovato");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                .body(resource);
    }

}