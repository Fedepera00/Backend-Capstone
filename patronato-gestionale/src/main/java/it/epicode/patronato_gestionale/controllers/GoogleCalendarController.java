package it.epicode.patronato_gestionale.controllers;

import com.google.api.services.calendar.model.Event;
import it.epicode.patronato_gestionale.dto.GoogleCalendarEventRequest;
import it.epicode.patronato_gestionale.services.GoogleCalendarService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private final GoogleCalendarService calendarService;

    public GoogleCalendarController(GoogleCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * Endpoint per recuperare gli eventi dal calendario.
     */
    @GetMapping("/listEvents")
    public List<Event> listEvents() throws GeneralSecurityException, IOException {
        return calendarService.getEvents();
    }

    /**
     * Endpoint per creare un nuovo evento.
     */
    @PostMapping("/createEvent")
    public Event createEvent(@RequestBody GoogleCalendarEventRequest request) throws GeneralSecurityException, IOException {
        return calendarService.createEvent(
                request.getSummary(),
                request.getLocation(),
                request.getDescription(),
                request.getStartDateTime(),
                request.getEndDateTime()
        );
    }
}