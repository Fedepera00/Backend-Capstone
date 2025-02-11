package it.epicode.patronato_gestionale.entities;

import it.epicode.patronato_gestionale.enums.FatturaStato;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

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

    // Nuovi campi aggiunti
    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false)
    private String codiceFiscale;

    @Column(nullable = false)
    private String indirizzo;

    @Column(nullable = true)
    private String telefono;

    @Column(nullable = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FatturaStato stato; // Stato della fattura (PAGATA, IN_ATTESA, SCADUTA)

    // Relazione con Dettaglio
    @OneToMany(mappedBy = "fattura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dettaglio> dettagli;

    public List<Dettaglio> getDettagli() {
        return dettagli;
    }

    public void setDettagli(List<Dettaglio> dettagli) {
        this.dettagli = dettagli;
    }
}