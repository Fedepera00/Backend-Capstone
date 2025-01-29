package it.epicode.patronato_gestionale.entities;

import it.epicode.patronato_gestionale.enums.StatoPratica;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Pratica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    @Column(nullable = false)
    private String descrizione;

    @Column(nullable = false, columnDefinition = "varchar(255) default 'Anonimo'")
    private String richiedente;

    @Column(nullable = false, columnDefinition = "varchar(255) default 'Altro'")
    private String categoria;

    private String note;

    @Column(nullable = false)
    private LocalDate dataCreazione;

    @Enumerated(EnumType.STRING)
    private StatoPratica stato;
}