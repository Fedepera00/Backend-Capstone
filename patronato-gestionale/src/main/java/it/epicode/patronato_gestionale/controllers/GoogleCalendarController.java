package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.services.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(@RequestParam String summary,
                                         @RequestParam String location,
                                         @RequestParam String description,
                                         @RequestParam String startDateTime,
                                         @RequestParam String endDateTime) {
        try {
            Event event = googleCalendarService.createEvent(summary, location, description, startDateTime, endDateTime);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore: " + e.getMessage());
        }
    }
}