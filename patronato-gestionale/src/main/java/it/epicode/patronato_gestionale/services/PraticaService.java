package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.repositories.PraticaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PraticaService {

    @Autowired
    private PraticaRepository praticaRepository;

    public Pratica createPratica(String titolo, String descrizione, String richiedente, String categoria, String note) {
        Pratica pratica = new Pratica();
        pratica.setTitolo(titolo);
        pratica.setDescrizione(descrizione);
        pratica.setRichiedente(richiedente);
        pratica.setCategoria(categoria);
        pratica.setNote(note);
        pratica.setDataCreazione(LocalDate.now());
        pratica.setStato(StatoPratica.IN_ATTESA);
        return praticaRepository.save(pratica);
    }

    public List<Pratica> getAllPratiche() {
        return praticaRepository.findAll();
    }

    public Pratica getPraticaById(Long id) {
        return praticaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pratica non trovata con ID: " + id));
    }

    public Pratica updatePratica(Long id, String titolo, String descrizione, StatoPratica stato) {
        Pratica pratica = getPraticaById(id);
        pratica.setTitolo(titolo);
        pratica.setDescrizione(descrizione);
        pratica.setStato(stato);
        return praticaRepository.save(pratica);
    }

    public void deletePratica(Long id) {
        praticaRepository.deleteById(id);
    }
}