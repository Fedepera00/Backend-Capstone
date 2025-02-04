package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.repositories.AppuntamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private AppuntamentoRepository appuntamentoRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * ?") // Esegui ogni giorno alle 8:00
    public void sendDailyReminders() {
        List<Appuntamento> tomorrowAppointments = appuntamentoRepository.findByDataOraBetween(
                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(23).withMinute(59)
        );

        for (Appuntamento appointment : tomorrowAppointments) {
            String subject = "Promemoria Appuntamento";
            String body = String.format(
                    "Ciao %s,\n\nTi ricordiamo il tuo appuntamento per il %s alle ore %s presso %s.\n\nGrazie!",
                    appointment.getNome(), appointment.getDataOra().toLocalDate(), appointment.getDataOra().toLocalTime(), appointment.getLuogo()
            );
            emailService.sendEmail(appointment.getEmail(), subject, body);
        }
    }
}