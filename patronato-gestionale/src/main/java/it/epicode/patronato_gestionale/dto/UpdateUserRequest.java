package it.epicode.patronato_gestionale.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password;
}