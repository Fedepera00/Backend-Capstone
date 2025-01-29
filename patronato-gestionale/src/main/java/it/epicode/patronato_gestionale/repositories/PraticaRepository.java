package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Pratica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PraticaRepository extends JpaRepository<Pratica, Long> {
}