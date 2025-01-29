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

    @Autowired
    private EmailService emailService;

    public Appuntamento createAppuntamento(String titolo, LocalDateTime dataOra, String luogo, String descrizione, String username) {
        AppUser utente = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));

        Appuntamento appuntamento = new Appuntamento();
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        appuntamento.setDescrizione(descrizione);
        appuntamento.setUtente(utente);

        Appuntamento salvato = appuntamentoRepository.save(appuntamento);

        // Invia email al destinatario
        emailService.sendEmail(
                utente.getUsername(),
                "Nuovo Appuntamento Creato",
                "Ciao " + utente.getUsername() + ",\n\nHai un nuovo appuntamento creato con i seguenti dettagli:\n" +
                        "Titolo: " + titolo + "\n" +
                        "Data e Ora: " + dataOra + "\n" +
                        "Luogo: " + luogo + "\n" +
                        "Descrizione: " + descrizione + "\n\nGrazie."
        );

        return salvato;
    }


    public List<Appuntamento> getAllAppuntamenti() {
        return appuntamentoRepository.findAll();
    }

    public Appuntamento getAppuntamentoById(Long id) {
        return appuntamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appuntamento non trovato con ID: " + id));
    }

    public Appuntamento updateAppuntamento(Long id, String titolo, LocalDateTime dataOra, String luogo) {
        Appuntamento appuntamento = getAppuntamentoById(id);
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        return appuntamentoRepository.save(appuntamento);
    }

    public void deleteAppuntamento(Long id) {
        appuntamentoRepository.deleteById(id);
    }
}