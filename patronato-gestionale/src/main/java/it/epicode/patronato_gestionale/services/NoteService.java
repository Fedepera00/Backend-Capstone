package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Note;
import it.epicode.patronato_gestionale.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    // Metodo per creare una nota
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    // Metodo per aggiornare una nota
    public Optional<Note> updateNote(Long id, Note note) {
        return noteRepository.findById(id).map(existingNote -> {
            existingNote.setText(note.getText());
            existingNote.setDate(note.getDate());
            return noteRepository.save(existingNote);
        });
    }

    // Metodo per cancellare una nota
    public boolean deleteNote(Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Metodo per trovare le note in base alla data
    public List<Note> findNotesByDate(LocalDate date) {
        return noteRepository.findByDate(date);
    }
}