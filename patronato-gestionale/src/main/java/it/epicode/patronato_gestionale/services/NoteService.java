package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Note;
import it.epicode.patronato_gestionale.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;


    public Note createNote(Note note) {
        if (note.getCreatedAt() == null) {
            note.setCreatedAt(LocalDateTime.now());
        }
        return noteRepository.save(note);
    }


    public Optional<Note> updateNote(Long id, Note note) {
        return noteRepository.findById(id).map(existingNote -> {
            existingNote.setText(note.getText());
            existingNote.setDate(note.getDate());
            return noteRepository.save(existingNote);
        });
    }


    public boolean deleteNote(Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public List<Note> findNotesByDateAndUser(LocalDate date, String username) {
        return noteRepository.findByDateAndUsername(date, username);
    }
}