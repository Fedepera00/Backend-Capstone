package it.epicode.patronato_gestionale.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    // La data della nota (formato YYYY-MM-DD)
    @Column(nullable = false)
    private LocalDate date;


    @Column(nullable = false)
    private LocalDateTime createdAt;


    @Column(nullable = false)
    private String username;
}