package it.epicode.patronato_gestionale.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppuntamentoRequest {
    private String titolo; // Campo obbligatorio
    private LocalDateTime dataOra;
    private String luogo;
    private String descrizione;
    private String username;
}