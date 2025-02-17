package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.repositories.AppuntamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private AppuntamentoRepository appuntamentoRepository;

    @Autowired
    private TwilioSmsService twilioSmsService;

    // Esegui ogni giorno alle 8:00 AM (usa cron secondo la tua timezone)
    @Scheduled(cron = "0 0 8 * * *", zone = "Europe/Rome")
    public void sendReminders() {
        // Recupera gli appuntamenti per il giorno successivo
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end = tomorrow.atTime(LocalTime.MAX);

        List<Appuntamento> appuntamenti = appuntamentoRepository.findByDataOraBetween(start, end);

        // Per ogni appuntamento, invia un SMS (assicurati che l'Appuntamento abbia un campo "telefono")
        for (Appuntamento app : appuntamenti) {
            // Costruisci il messaggio
            String message = String.format("Promemoria: Il tuo appuntamento '%s' è previsto per domani alle %s presso %s.",
                    app.getTitolo(),
                    app.getDataOra().toLocalTime().toString(),
                    app.getLuogo());
            // Supponendo che il campo "telefono" contenga il numero nel formato internazionale (es: +391234567890)
            twilioSmsService.sendSms(app.getTelefono(), message);
        }
    }
}