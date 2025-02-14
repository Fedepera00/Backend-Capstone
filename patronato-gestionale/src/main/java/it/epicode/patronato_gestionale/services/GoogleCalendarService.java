package it.epicode.patronato_gestionale.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Patronato Gestionale";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String GOOGLE_CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar";

    @Value("${google.calendar.credentials.path}")
    private String credentialsPath;

    @Value("${google.calendar.id}")
    private String calendarId;

    /**
     * Inizializza il servizio Google Calendar leggendo le credenziali dal classpath.
     *
     * @return il servizio Google Calendar autenticato.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private Calendar getCalendarService() throws GeneralSecurityException, IOException {
        System.out.println("📌 [DEBUG] Caricamento file JSON delle credenziali dal classpath: " + credentialsPath);

        // Carica il file delle credenziali dal classpath
        InputStream inputStream = getClass().getResourceAsStream("/" + credentialsPath);
        if (inputStream == null) {
            throw new IOException("❌ Il file JSON delle credenziali NON è stato trovato nel classpath: " + credentialsPath);
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(Collections.singleton(GOOGLE_CALENDAR_SCOPE));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Recupera la lista di eventi dal calendario.
     *
     * @return Lista di eventi.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public List<Event> getEvents() throws GeneralSecurityException, IOException {
        System.out.println("📌 [DEBUG] Recupero eventi dal calendario...");
        Calendar service = getCalendarService();

        Events events = service.events().list(calendarId)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        System.out.println("✅ [DEBUG] Numero di eventi trovati: " + events.getItems().size());
        return events.getItems();
    }

    /**
     * Crea un nuovo evento sul calendario.
     *
     * @param summary       Titolo dell'evento.
     * @param location      Luogo dell'evento.
     * @param description   Descrizione dell'evento.
     * @param startDateTime Data/ora di inizio (formato ISO 8601, es. "2025-02-14T09:00:00+01:00").
     * @param endDateTime   Data/ora di fine (formato ISO 8601).
     * @return L'evento creato.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public Event createEvent(String summary, String location, String description,
                             String startDateTime, String endDateTime) throws GeneralSecurityException, IOException {
        System.out.println("📌 [DEBUG] Creazione evento: " + summary);
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description)
                .setVisibility("public")
                .setStatus("confirmed");

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
                .setTimeZone("Europe/Rome");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
                .setTimeZone("Europe/Rome");
        event.setEnd(end);

        event = service.events().insert(calendarId, event).execute();

        System.out.println("✅ [SUCCESSO] Evento creato con successo: " + event.getHtmlLink());
        return event;
    }
}