package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByDate(LocalDate date); // Assicurati che il nome del metodo sia corretto
}