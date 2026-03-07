package es.footleague.app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "match_events")
public class MatchEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int minute;

    @Column(nullable = false)
    private String type; // "GOAL", "CARD", "SUBSTITUTION"

    private String namePlayer;
    private String namePlayerOut;
    private String namePlayerIn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    public MatchEvent() {
    }

    public MatchEvent(String type, int minute, String namePlayer, Match match, Team team) {
        this.type = type;
        this.minute = minute;
        this.namePlayer = namePlayer;
        this.match = match;
        this.team = team;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getNamePlayerOut() {
        return namePlayerOut;
    }

    public void setNamePlayerOut(String namePlayerOut) {
        this.namePlayerOut = namePlayerOut;
    }

    public String getNamePlayerIn() {
        return namePlayerIn;
    }

    public void setNamePlayerIn(String namePlayerIn) {
        this.namePlayerIn = namePlayerIn;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    // Método para devolver el icono según el tipo de evento
    public String getIcon() {
        if (type == null)
            return "•";
        return switch (type.toUpperCase()) {
            case "GOAL" -> "⚽";
            case "YELLOW CARD" -> "🟨";
            case "RED_CARD" -> "🟥";
            case "SUBSTITUTION" -> "🔄";
            default -> "•";
        };
    }

    // Método para generar la descripción automática que pedía tu HTML
    public String getDescription() {
        if (type == null) return "";
        return switch (type.toUpperCase()) {
        case "GOAL" -> "Gol de " + namePlayer;
        case "CARD" -> "Tarjeta para " + namePlayer;
        case "SUBSTITUTION" -> "Cambio: 🟢 " + namePlayerIn + " 🔴 " + namePlayerOut;
        default -> type + " - " + namePlayer;
    };
    }
}
