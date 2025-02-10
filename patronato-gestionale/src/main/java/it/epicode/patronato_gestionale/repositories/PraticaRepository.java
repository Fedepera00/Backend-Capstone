package it.epicode.patronato_gestionale.repositories;

import it.epicode.patronato_gestionale.entities.Pratica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PraticaRepository extends JpaRepository<Pratica, Long>, JpaSpecificationExecutor<Pratica> {
}