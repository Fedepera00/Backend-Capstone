package it.epicode.patronato_gestionale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class FatturaRequest {

    @NotBlank(message = "Il numero della fattura è obbligatorio")
    private String numero;

    @NotNull(message = "La data di emissione è obbligatoria")
    private LocalDate dataEmissione;

    @NotNull(message = "L'importo è obbligatorio")
    private Double importo;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String descrizione;

    // Campi aggiuntivi
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    private String codiceFiscale;

    @NotBlank(message = "L'indirizzo è obbligatorio")
    private String indirizzo;

    private String telefono;
    private String email;
}