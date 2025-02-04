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

    @Autowired
    private EmailService emailService;

    public Appuntamento createAppuntamento(String titolo, LocalDateTime dataOra, String luogo, String descrizione,
                                           String nome, String cognome, String stato, String email, String username) {
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
        appuntamento.setEmail(email);
        appuntamento.setUtente(utente);

        Appuntamento savedAppuntamento = appuntamentoRepository.save(appuntamento);

        // Invio email
        String subject = "Conferma Appuntamento: " + titolo;
        String body = String.format("Ciao %s,\n\nIl tuo appuntamento è stato confermato per il %s alle ore %s presso %s.\n\nDescrizione: %s\n\nGrazie, \nPatronato Gestionale",
                nome, dataOra.toLocalDate(), dataOra.toLocalTime(), luogo, descrizione);
        emailService.sendEmail(email, subject, body);

        return savedAppuntamento;
    }

    public List<Appuntamento> filterAppuntamenti(String nome, String cognome, String stato, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return appuntamentoRepository.findByNomeIgnoreCaseContainingAndCognomeIgnoreCaseContainingAndStatoIgnoreCaseContainingAndDataOraBetween(
                    nome != null ? nome : "",
                    cognome != null ? cognome : "",
                    stato != null ? stato : "",
                    startDate,
                    endDate
            );
        } else {
            return appuntamentoRepository.findByNomeIgnoreCaseContainingAndCognomeIgnoreCaseContainingAndStatoIgnoreCaseContaining(
                    nome != null ? nome : "",
                    cognome != null ? cognome : "",
                    stato != null ? stato : ""
            );
        }
    }

    public Appuntamento updateAppuntamento(Long id, String titolo, LocalDateTime dataOra, String luogo,
                                           String nome, String cognome, String stato, String email) {
        Appuntamento appuntamento = getAppuntamentoById(id);
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        appuntamento.setNome(nome);
        appuntamento.setCognome(cognome);
        appuntamento.setStato(stato);
        appuntamento.setEmail(email);

        Appuntamento updatedAppuntamento = appuntamentoRepository.save(appuntamento);

        // Invio email
        String subject = "Aggiornamento Appuntamento: " + titolo;
        String body = String.format("Ciao %s,\n\nIl tuo appuntamento è stato aggiornato:\n\nData: %s\nOrario: %s\nLuogo: %s\nStato: %s\n\nGrazie, \nPatronato Gestionale",
                nome, dataOra.toLocalDate(), dataOra.toLocalTime(), luogo, stato);
        emailService.sendEmail(email, subject, body);

        return updatedAppuntamento;
    }

    public Appuntamento getAppuntamentoById(Long id) {
        return appuntamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appuntamento non trovato con ID: " + id));
    }

    public void deleteAppuntamento(Long id) {
        Appuntamento appuntamento = getAppuntamentoById(id);
        appuntamentoRepository.deleteById(id);

        // Invio email di cancellazione
        String subject = "Cancellazione Appuntamento: " + appuntamento.getTitolo();
        String body = String.format("Ciao %s,\n\nIl tuo appuntamento per il %s alle ore %s presso %s è stato cancellato.\n\nGrazie, \nPatronato Gestionale",
                appuntamento.getNome(), appuntamento.getDataOra().toLocalDate(), appuntamento.getDataOra().toLocalTime(), appuntamento.getLuogo());
        emailService.sendEmail(appuntamento.getEmail(), subject, body);
    }
}