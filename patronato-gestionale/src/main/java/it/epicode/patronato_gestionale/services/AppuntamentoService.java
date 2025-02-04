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
import java.util.stream.Collectors;

@Service
public class AppuntamentoService {

    @Autowired
    private AppuntamentoRepository appuntamentoRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Appuntamento createAppuntamento(String titolo, LocalDateTime dataOra, String luogo, String descrizione, String nome, String cognome, String stato, String username) {
        AppUser utente = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));

        Appuntamento appuntamento = new Appuntamento();
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        appuntamento.setDescrizione(descrizione);
        appuntamento.setNome(nome);
        appuntamento.setCognome(cognome);
        appuntamento.setStato(stato);
        appuntamento.setUtente(utente);

        return appuntamentoRepository.save(appuntamento);
    }

    public List<Appuntamento> filterAppuntamenti(String nome, String cognome, String stato) {
        List<Appuntamento> appuntamenti = appuntamentoRepository.findAll();

        if (nome != null) {
            appuntamenti = appuntamenti.stream()
                    .filter(a -> a.getNome().equalsIgnoreCase(nome))
                    .collect(Collectors.toList());
        }
        if (cognome != null) {
            appuntamenti = appuntamenti.stream()
                    .filter(a -> a.getCognome().equalsIgnoreCase(cognome))
                    .collect(Collectors.toList());
        }
        if (stato != null) {
            appuntamenti = appuntamenti.stream()
                    .filter(a -> a.getStato().equalsIgnoreCase(stato))
                    .collect(Collectors.toList());
        }
        return appuntamenti;
    }

    public Appuntamento updateAppuntamento(Long id, String titolo, LocalDateTime dataOra, String luogo, String nome, String cognome, String stato) {
        Appuntamento appuntamento = getAppuntamentoById(id);
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        appuntamento.setNome(nome);
        appuntamento.setCognome(cognome);
        appuntamento.setStato(stato);
        return appuntamentoRepository.save(appuntamento);
    }

    public Appuntamento getAppuntamentoById(Long id) {
        return appuntamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appuntamento non trovato con ID: " + id));
    }

    public void deleteAppuntamento(Long id) {
        appuntamentoRepository.deleteById(id);
    }
}