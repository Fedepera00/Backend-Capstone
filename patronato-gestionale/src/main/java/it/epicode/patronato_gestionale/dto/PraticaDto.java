package it.epicode.patronato_gestionale.dto;

import it.epicode.patronato_gestionale.enums.StatoPratica;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PraticaDto extends RepresentationModel<PraticaDto> {

    private Long id;
    private String titolo;
    private String descrizione;
    private String richiedente;
    private String codiceFiscale;
    private String categoria;
    private String note;
    private String pdfUrl;
    private LocalDate dataCreazione;
    private LocalDateTime ultimaModifica;
    private StatoPratica stato; // Usa direttamente l'enum
}