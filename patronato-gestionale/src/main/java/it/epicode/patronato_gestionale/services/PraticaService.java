package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.repositories.PraticaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PraticaService {

    @Autowired
    private PraticaRepository praticaRepository;

    @Autowired
    private GoogleDriveService googleDriveService;

    /**
     * ✅ Metodo aggiunto per ottenere pratiche paginabili
     */
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

    /**
     * ✅ Carica il PDF su Google Drive e salva il link nella pratica
     */
    public String uploadPdf(Long id, MultipartFile file) throws Exception {
        Pratica pratica = getPraticaById(id);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Il file è vuoto!");
        }

        // Esempio di controllo basato sull'estensione del filename
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Il file deve essere un PDF valido");
        }

        // Oppure, se preferisci usare il content type, puoi usare:
        // String contentType = file.getContentType();
        // if (contentType == null || (!contentType.equals("application/pdf") && !contentType.equals("application/octet-stream"))) {
        //     throw new IllegalArgumentException("Il file deve essere un PDF valido");
        // }

        // Carica il file su Google Drive e ottiene il link
        String fileLink = googleDriveService.uploadFile(file);

        // Salva il link su Google Drive nella pratica
        pratica.setDriveUrl(fileLink);
        praticaRepository.save(pratica);

        return fileLink;
    }

    /**
     * ✅ Recupera il link pubblico del PDF associato alla pratica
     */
    public String getPdfLink(Long id) {
        Pratica pratica = getPraticaById(id);

        if (pratica.getDriveUrl() == null) {
            throw new IllegalArgumentException("Nessun file PDF associato a questa pratica");
        }

        return pratica.getDriveUrl();
    }

    /**
     * ✅ Cerca pratiche filtrando per titolo, richiedente, stato e data di creazione
     */
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
}