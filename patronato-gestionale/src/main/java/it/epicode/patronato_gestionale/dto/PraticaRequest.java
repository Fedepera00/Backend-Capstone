package it.epicode.patronato_gestionale.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PraticaRequest {
    @NotBlank(message = "Il titolo è obbligatorio")
    private String titolo;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String descrizione;

    @NotBlank(message = "Il richiedente è obbligatorio")
    private String richiedente;

    @NotBlank(message = "La categoria è obbligatoria")
    private String categoria;

    private String note;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    private String codiceFiscale;

    @NotBlank(message = "Lo stato è obbligatorio")
    private String stato;
}