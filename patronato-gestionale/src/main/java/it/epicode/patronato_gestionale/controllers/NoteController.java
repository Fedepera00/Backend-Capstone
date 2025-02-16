package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.entities.Note;
import it.epicode.patronato_gestionale.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    // GET /api/notes/search?date=2025-02-15
    // Restituisce solo le note del giorno per l'utente autenticato
    @GetMapping("/search")
    public ResponseEntity<List<Note>> getNotesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Principal principal) {
        String username = principal.getName();
        List<Note> notes = noteService.findNotesByDateAndUser(date, username);
        return ResponseEntity.ok(notes);
    }

    // POST /api/notes
    // Quando si crea una nota, viene assegnato il proprietario (username) in base all'utente autenticato
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note, Principal principal) {
        note.setUsername(principal.getName());
        Note created = noteService.createNote(note);
        return ResponseEntity.status(201).body(created);
    }

    // PUT /api/notes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        return noteService.updateNote(id, note)
                .map(updatedNote -> ResponseEntity.ok(updatedNote))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/notes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        if (noteService.deleteNote(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}