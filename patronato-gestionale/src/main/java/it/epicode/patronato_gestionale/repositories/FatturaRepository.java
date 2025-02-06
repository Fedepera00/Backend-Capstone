package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Fattura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface FatturaRepository extends JpaRepository<Fattura, Long> {
    boolean existsByNumero(String numero);

    Page<Fattura> findAll(Pageable pageable);
}