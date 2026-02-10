package es.model;

import jakarta.persistence.*;
import java.util.List;

public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] escudo;

    // Campos para tu tabla classification.html
    private int partidosJugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    private int puntos;

    // Getters y Setters
}
