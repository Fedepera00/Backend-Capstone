package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {

    List<Appuntamento> findByDataOraBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Appuntamento> findByNomeIgnoreCaseContainingAndCognomeIgnoreCaseContainingAndStatoIgnoreCaseContaining(String nome, String cognome, String stato);

    List<Appuntamento> findByNomeIgnoreCaseContainingAndCognomeIgnoreCaseContainingAndStatoIgnoreCaseContainingAndDataOraBetween(String nome, String cognome, String stato, LocalDateTime startDate, LocalDateTime endDate);
}