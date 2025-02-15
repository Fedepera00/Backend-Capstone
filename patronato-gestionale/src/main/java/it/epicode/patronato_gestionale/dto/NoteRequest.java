package it.epicode.patronato_gestionale.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class NoteRequest {
    private String text;
    private LocalDate date;
}