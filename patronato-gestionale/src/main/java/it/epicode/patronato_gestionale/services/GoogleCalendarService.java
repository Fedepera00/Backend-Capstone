package it.epicode.patronato_gestionale.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.http.HttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarService {

    @Value("${google.calendar.credentials.path}")
    private String credentialsPath;

    @Value("${google.calendar.id}")
    private String calendarId;

    private static final String APPLICATION_NAME = "Patronato Gestionale";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Calendar getCalendarService() throws GeneralSecurityException, IOException {
        System.out.println("📌 Percorso file JSON: " + credentialsPath); // Debug

        InputStream inputStream = new FileInputStream(new File("src/main/resources/google-calendar-gestionale-patronato.json"));
        if (inputStream == null) {
            throw new FileNotFoundException("❌ Il file NON è stato trovato: " + credentialsPath);
        }

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public List<Event> getEvents() throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService();
        Events events = service.events().list(calendarId)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
    /**
     * Crea un evento sul calendario.
     *
     * @param summary       Il titolo dell'evento.
     * @param location      La sede dell'evento.
     * @param description   La descrizione dell'evento.
     * @param startDateTime Data/ora di inizio in formato ISO 8601 (es. "2025-02-14T09:00:00+01:00").
     * @param endDateTime   Data/ora di fine in formato ISO 8601.
     * @return L'evento creato.
     */
    public Event createEvent(String summary, String location, String description,
                             String startDateTime, String endDateTime)
            throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description);

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
                .setTimeZone("Europe/Rome");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
                .setTimeZone("Europe/Rome");
        event.setEnd(end);

        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Evento creato: %s\n", event.getHtmlLink());
        return event;
    }

}
