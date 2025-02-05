package it.epicode.patronato_gestionale.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Fattura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero; // Numero univoco della fattura

    @Column(nullable = false)
    private LocalDate dataEmissione;

    @Column(nullable = false)
    private Double importo;

    @Column(nullable = false)
    private String descrizione;
}