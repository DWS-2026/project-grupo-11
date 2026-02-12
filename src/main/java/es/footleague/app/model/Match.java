package es.model;

import java.time.LocalDateTime;
import java.util.List;

public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha; // Para "01 de Febrero, 16:15"
    private String estadio;     // Para "Santiago Bernabéu"
    private String estado;      // Para el badge de "Finalizado"

    @ManyToOne
    private Equipo equipoLocal;

    @ManyToOne
    private Equipo equipoVisitante;

    private int golesLocal;
    private int golesVisitante;

    // Los "Sucesos del Partido" de tu HTML
    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL)
    private List<EventoPartido> eventos;

    // Getters y Setters
}
