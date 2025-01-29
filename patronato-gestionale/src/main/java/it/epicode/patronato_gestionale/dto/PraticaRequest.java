package it.epicode.patronato_gestionale.dto;

import lombok.Data;

@Data
public class PraticaRequest {
    private String titolo;
    private String descrizione;
    private String richiedente;
    private String categoria;
    private String note;
}