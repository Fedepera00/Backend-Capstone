package it.epicode.patronato_gestionale.entities;

import it.epicode.patronato_gestionale.auth.AppUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Appuntamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    @Column(nullable = false)
    private LocalDateTime dataOra;

    @Column(nullable = false)
    private String luogo;

    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private AppUser utente;
}