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

    public boolean getIsLocal() {
        if (this.match == null || this.team == null) return true;
        // It checks if the team of the event is the local team of the match. 
        // If so, it returns true; otherwise, it returns false (visitor).
        return this.team.getId().equals(this.match.getLocalTeam().getId());
    }

    public boolean getIsVisitor() {
        if (this.match == null || this.team == null) return false;
        return this.team.getId().equals(this.match.getVisitorTeam().getId());
    }

    public boolean getIsGoal() {
        return "GOAL".equalsIgnoreCase(this.type);
    }

    public boolean getIsYellow() {
        // It checks if the type of the event is "YELLOW_CARD", "YELLOW CARD" or just "CARD" 
        // (in case we want to use a generic "CARD" type for both yellow and red). If so, 
        // it returns true; otherwise, it returns false.
        return "YELLOW_CARD".equalsIgnoreCase(this.type) || 
               "YELLOW CARD".equalsIgnoreCase(this.type) || 
               "CARD".equalsIgnoreCase(this.type);
    }

    public boolean getIsRed() {
        return "RED_CARD".equalsIgnoreCase(this.type);
    }

    public boolean getIsSubstitution() {
        return "SUBSTITUTION".equalsIgnoreCase(this.type);
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

    // Method to get the icon for the event type, used in the HTML to display 
    // the correct icon for each event
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

    // MMethod to generate the automatic description that your HTML requested
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
