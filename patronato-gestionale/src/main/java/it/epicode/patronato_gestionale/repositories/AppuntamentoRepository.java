package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {
}