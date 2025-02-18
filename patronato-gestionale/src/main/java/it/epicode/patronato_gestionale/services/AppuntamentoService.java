package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.auth.AppUser;
import it.epicode.patronato_gestionale.auth.AppUserRepository;
import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.repositories.AppuntamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.*;
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

    @Autowired
    private GoogleCalendarService googleCalendarService;

    public Appuntamento createAppuntamento(String titolo,
                                           LocalDateTime dataOra,
                                           String luogo,
                                           String descrizione,
                                           String nome,
                                           String cognome,
                                           String stato,
                                           String email,
                                           String username,
                                           String telefono) {


        LocalDateTime startInterval = dataOra.minusMinutes(15);
        LocalDateTime endInterval = dataOra.plusMinutes(15);
        List<Appuntamento> conflict = appuntamentoRepository.findByDataOraBetween(startInterval, endInterval);
        if (!conflict.isEmpty()) {
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
        appuntamento.setTelefono(telefono);
        appuntamento.setUtente(utente);


        Appuntamento savedAppuntamento = appuntamentoRepository.save(appuntamento);


        String subject = "Conferma Appuntamento: " + titolo;
        String body = String.format("Ciao %s,\n\nIl tuo appuntamento è stato confermato per il %s alle ore %s presso %s.\n\nDescrizione: %s\n\nGrazie,\nPatronato Gestionale",
                nome, dataOra.toLocalDate(), dataOra.toLocalTime(), luogo, descrizione);
        emailService.sendEmail(email, subject, body);

        return savedAppuntamento;
    }


    public Appuntamento updateAppuntamento(Long id,
                                           String titolo,
                                           LocalDateTime dataOra,
                                           String luogo,
                                           String descrizione,
                                           String nome,
                                           String cognome,
                                           String stato,
                                           String email,
                                           String telefono) {
        Appuntamento appuntamento = getAppuntamentoById(id);
        appuntamento.setTitolo(titolo);
        appuntamento.setDataOra(dataOra);
        appuntamento.setLuogo(luogo);
        appuntamento.setDescrizione(descrizione);
        appuntamento.setNome(nome);
        appuntamento.setCognome(cognome);
        appuntamento.setStato(stato);
        appuntamento.setEmail(email);
        appuntamento.setTelefono(telefono);

        Appuntamento updatedAppuntamento = appuntamentoRepository.save(appuntamento);


        String subject = "Aggiornamento Appuntamento: " + titolo;
        String body = String.format("Ciao %s,\n\nIl tuo appuntamento è stato aggiornato:\n\nData: %s\nOrario: %s\nLuogo: %s\nStato: %s\n\nGrazie,\nPatronato Gestionale",
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


        String subject = "Cancellazione Appuntamento: " + appuntamento.getTitolo();
        String body = String.format("Ciao %s,\n\nIl tuo appuntamento per il %s alle ore %s presso %s è stato cancellato.\n\nGrazie,\nPatronato Gestionale",
                appuntamento.getNome(), appuntamento.getDataOra().toLocalDate(), appuntamento.getDataOra().toLocalTime(), appuntamento.getLuogo());
        emailService.sendEmail(appuntamento.getEmail(), subject, body);
    }

    public List<LocalTime> getAvailableSlots(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return Collections.emptyList();
        }
        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime time = LocalTime.of(9, 0);
        LocalTime morningEnd = LocalTime.of(13, 0);
        while (time.isBefore(morningEnd)) {
            allSlots.add(time);
            time = time.plusMinutes(15);
        }
        time = LocalTime.of(15, 0);
        LocalTime afternoonEnd = LocalTime.of(18, 0);
        while (time.isBefore(afternoonEnd)) {
            allSlots.add(time);
            time = time.plusMinutes(15);
        }
        LocalDateTime morningStart = date.atTime(9, 0);
        LocalDateTime morningEndDT = date.atTime(13, 0);
        LocalDateTime afternoonStart = date.atTime(15, 0);
        LocalDateTime afternoonEndDT = date.atTime(18, 0);
        List<Appuntamento> morningApps = appuntamentoRepository.findByDataOraBetween(morningStart, morningEndDT);
        List<Appuntamento> afternoonApps = appuntamentoRepository.findByDataOraBetween(afternoonStart, afternoonEndDT);
        Set<LocalTime> bookedSlots = new HashSet<>();
        morningApps.forEach(a -> bookedSlots.add(a.getDataOra().toLocalTime().withSecond(0).withNano(0)));
        afternoonApps.forEach(a -> bookedSlots.add(a.getDataOra().toLocalTime().withSecond(0).withNano(0)));
        return allSlots.stream().filter(slot -> !bookedSlots.contains(slot)).collect(Collectors.toList());
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
}