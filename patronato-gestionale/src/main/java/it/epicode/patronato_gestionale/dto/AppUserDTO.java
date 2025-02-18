package it.epicode.patronato_gestionale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class AppUserDTO {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private Set<String> roles;
}