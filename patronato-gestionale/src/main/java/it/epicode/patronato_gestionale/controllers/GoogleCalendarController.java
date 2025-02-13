package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.dto.GoogleCalendarEventRequest;
import it.epicode.patronato_gestionale.services.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping("/listEvents")
    public List<Event> listEvents() {
        try {
            return googleCalendarService.getEvents();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    @PostMapping("/createEvent")
    public Event createEvent(@RequestBody GoogleCalendarEventRequest request) {
        try {
            return googleCalendarService.createEvent(
                    request.getSummary(),
                    request.getLocation(),
                    request.getDescription(),
                    request.getStartDateTime(),
                    request.getEndDateTime()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}