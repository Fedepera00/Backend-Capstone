package it.epicode.patronato_gestionale.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppuntamentoRequest {
    private String titolo;
    private LocalDateTime dataOra;
    private String luogo;
    private String descrizione;
    private String nome;
    private String cognome;
    private String stato;
    private String email;
    private String telefono;
}