package es.model;

import jakarta.persistence.*;

@Entity
public class MatchEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minuto;
    private String descripcion; // Ej: "Gol de Vinícius Jr."
    private String tipo;        // "GOL", "TARJETA_AMARILLA", "TARJETA_ROJA"

    @ManyToOne
    private Partido partido;

    // Getters y Setters
}
