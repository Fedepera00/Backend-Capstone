package it.epicode.patronato_gestionale.entities;

import it.epicode.patronato_gestionale.enums.StatoPratica;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String richiedente;

    @Column(nullable = false)
    private String codiceFiscale;

    @Column(nullable = false)
    private String categoria;

    private String note;

    @Column(nullable = true)
    private String driveUrl;

    @Column(nullable = false)
    private LocalDate dataCreazione;

    @Column(nullable = false)
    private LocalDateTime ultimaModifica;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPratica stato;

    public String getDriveUrl() {
        return driveUrl;
    }

    public void setDriveUrl(String driveUrl) {
        this.driveUrl = driveUrl;
    }
}