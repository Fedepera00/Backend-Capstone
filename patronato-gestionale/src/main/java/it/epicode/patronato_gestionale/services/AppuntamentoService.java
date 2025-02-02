package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.auth.AppUser;
import it.epicode.patronato_gestionale.repositories.AppuntamentoRepository;
import it.epicode.patronato_gestionale.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppuntamentoService {

    @Autowired
    private AppuntamentoRepository appuntamentoRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Appuntamento createAppuntamento(String titolo, LocalDateTime dataOra, String luogo, String descrizione, String nome, String cognome, String username) {
        AppUser utente = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));

        Appuntamento appuntamento = new Appuntamento();
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        appuntamento.setDescrizione(descrizione);
        appuntamento.setNome(nome); // Imposta il nome
        appuntamento.setCognome(cognome); // Imposta il cognome
        appuntamento.setUtente(utente);

        return appuntamentoRepository.save(appuntamento);
    }

    public List<Appuntamento> getAllAppuntamenti() {
        return appuntamentoRepository.findAll();
    }

    public Appuntamento getAppuntamentoById(Long id) {
        return appuntamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appuntamento non trovato con ID: " + id));
    }

    public Appuntamento updateAppuntamento(Long id, String titolo, LocalDateTime dataOra, String luogo, String nome, String cognome) {
        Appuntamento appuntamento = getAppuntamentoById(id);
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        appuntamento.setNome(nome); // Aggiorna il nome
        appuntamento.setCognome(cognome); // Aggiorna il cognome
        return appuntamentoRepository.save(appuntamento);
    }

    public void deleteAppuntamento(Long id) {
        appuntamentoRepository.deleteById(id);
    }
}