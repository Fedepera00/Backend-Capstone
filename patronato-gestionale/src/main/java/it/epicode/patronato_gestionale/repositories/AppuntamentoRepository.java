package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {
    // Trova appuntamenti tra due date specifiche
    List<Appuntamento> findByDataOraBetween(LocalDateTime startDate, LocalDateTime endDate);
}