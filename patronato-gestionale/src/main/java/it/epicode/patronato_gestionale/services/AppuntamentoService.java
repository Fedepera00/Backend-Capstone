package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.auth.AppUser;
import it.epicode.patronato_gestionale.repositories.AppuntamentoRepository;
import it.epicode.patronato_gestionale.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
        // Verifica se esiste già un appuntamento entro 15 minuti dall'orario richiesto
        LocalDateTime startInterval = dataOra.minusMinutes(15);
        LocalDateTime endInterval = dataOra.plusMinutes(15);
        List<Appuntamento> conflict = appuntamentoRepository.findByDataOraBetween(startInterval, endInterval);
        if (!conflict.isEmpty()) {
            // Puoi anche lanciare una eccezione custom o una IllegalStateException
            throw new IllegalStateException("Orario non disponibile. Deve esserci almeno un intervallo di 15 minuti tra gli appuntamenti.");
        }

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
    /**
     * Restituisce gli slot disponibili per una data (dal lunedì al venerdì)
     * Considera due fasce: 9:00-13:00 e 15:00-18:00, con step di 15 minuti.
     */
    public List<LocalTime> getAvailableSlots(LocalDate date) {
        // Controlla che il giorno sia feriale (lunedì-venerdì)
        DayOfWeek day = date.getDayOfWeek();
        if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY){
            return Collections.emptyList();
        }

        List<LocalTime> allSlots = new ArrayList<>();

        // Fascia mattutina: 9:00-13:00
        LocalTime time = LocalTime.of(9, 0);
        LocalTime morningEnd = LocalTime.of(13, 0);
        while (time.isBefore(morningEnd)) {
            allSlots.add(time);
            time = time.plusMinutes(15);
        }

        // Fascia pomeridiana: 15:00-18:00
        time = LocalTime.of(15, 0);
        LocalTime afternoonEnd = LocalTime.of(18, 0);
        while (time.isBefore(afternoonEnd)) {
            allSlots.add(time);
            time = time.plusMinutes(15);
        }

        // Recupera gli appuntamenti già prenotati per la data in questione (solo nelle fasce orarie)
        LocalDateTime morningStartDateTime = date.atTime(9, 0);
        LocalDateTime morningEndDateTime = date.atTime(13, 0);
        LocalDateTime afternoonStartDateTime = date.atTime(15, 0);
        LocalDateTime afternoonEndDateTime = date.atTime(18, 0);

        List<Appuntamento> morningAppointments = appuntamentoRepository.findByDataOraBetween(morningStartDateTime, morningEndDateTime);
        List<Appuntamento> afternoonAppointments = appuntamentoRepository.findByDataOraBetween(afternoonStartDateTime, afternoonEndDateTime);

        // Estrai gli orari prenotati (normalizzando a HH:mm)
        Set<LocalTime> bookedSlots = new HashSet<>();
        morningAppointments.forEach(a -> bookedSlots.add(a.getDataOra().toLocalTime().withSecond(0).withNano(0)));
        afternoonAppointments.forEach(a -> bookedSlots.add(a.getDataOra().toLocalTime().withSecond(0).withNano(0)));

        // Rimuovi gli slot prenotati
        List<LocalTime> availableSlots = allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
        return availableSlots;
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