package es.footleague.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "match_events")
public class MatchEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minute;
    private String type; // "GOAL", "CARD", "SUBSTITUTION"
    private String namePlayer; 
    private int numberPlayer;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    protected MatchEvent(){}

    public MatchEvent(String type, int minute, String namePlayer, int numberPlayer, Match match){
        this.type = type;
        this.minute = minute;
        this.namePlayer = namePlayer;
        this.numberPlayer = numberPlayer;
        this.match = match;
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

    public int getNumberPlayer() {
        return numberPlayer;
    }

    public void setNumberPlayer(int numberPlayer) {
        this.numberPlayer = numberPlayer;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    

    
}
