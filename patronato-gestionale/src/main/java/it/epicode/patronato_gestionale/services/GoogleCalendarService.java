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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Patronato Gestionale";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String GOOGLE_CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar";



    @Value("${google.calendar.credentials.json}")
    private String credentialsJson;

    @Value("${google.calendar.id}")
    private String calendarId;

    private Calendar getCalendarService() throws GeneralSecurityException, IOException {
        // Converti la stringa in un InputStream
        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(
                credentialsJson.getBytes(StandardCharsets.UTF_8)
        );

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singleton(GOOGLE_CALENDAR_SCOPE));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
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

        System.out.println("✅ [DEBUG] Numero di eventi trovati: " + events.getItems().size());
        return events.getItems();
    }


    public Event createEvent(String summary, String location, String description,
                             String startDateTime, String endDateTime) throws GeneralSecurityException, IOException {
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