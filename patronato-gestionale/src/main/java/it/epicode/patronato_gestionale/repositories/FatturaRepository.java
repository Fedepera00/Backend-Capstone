package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Fattura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FatturaRepository extends JpaRepository<Fattura, Long> {
    boolean existsByNumero(String numero);
}