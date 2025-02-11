package it.epicode.patronato_gestionale.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Dettaglio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descrizione;

    @Column(nullable = false)
    private int quantita;

    @Column(nullable = false)
    private double prezzoUnitario;

    @ManyToOne
    @JoinColumn(name = "fattura_id", nullable = false)
    private Fattura fattura;

    public double getTotale() {
        return quantita * prezzoUnitario;
    }
}