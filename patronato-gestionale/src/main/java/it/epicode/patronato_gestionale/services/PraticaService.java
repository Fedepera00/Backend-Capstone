package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.repositories.PraticaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    public String uploadPdf(Long id, MultipartFile file) throws Exception {
        // Stampa l'ID della pratica e il nome del file
        System.out.println("Upload PDF: ID pratica = " + id);
        System.out.println("Nome file ricevuto = " + (file != null ? file.getOriginalFilename() : "Nessun file ricevuto"));

        Pratica pratica = getPraticaById(id);

        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            System.out.println("Errore: File vuoto o non è un PDF valido");
            throw new IllegalArgumentException("Il file deve essere un PDF valido");
        }

        // Percorso del file
        String directory = "uploads/pdf/";
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println("Creata directory: " + directory);
        }

        String fileName = id + "_" + file.getOriginalFilename();
        Path filePath = path.resolve(fileName);

        // Salvataggio del file
        Files.copy(file.getInputStream(), filePath);
        System.out.println("File salvato in: " + filePath);

        // Aggiorna la pratica con il percorso del file
        String relativePath = directory + fileName;
        pratica.setPdfUrl(relativePath);
        praticaRepository.save(pratica);
        System.out.println("Pratica aggiornata con PDF URL: " + relativePath);

        return fileName;
    }
    public ResponseEntity<Resource> downloadPdf(Long id) throws Exception {
        Pratica pratica = getPraticaById(id);

        if (pratica.getPdfUrl() == null) {
            throw new IllegalArgumentException("Nessun file PDF associato a questa pratica");
        }

        Path filePath = Paths.get("uploads/pdf/", pratica.getPdfUrl());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("File non trovato o non leggibile");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                .body(resource);
    }

}