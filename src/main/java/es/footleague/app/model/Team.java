package es.footleague.app.model;

import jakarta.persistence.*;

import java.sql.Blob;
import java.util.Base64;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String stadiumName;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private int playedMatchs;

    @Column(nullable = false)
    private int wins;

    private int draws;

    @Column(nullable = false)
    private int losses;

    @Lob
    @Column(name = "logo_data", columnDefinition = "LONGBLOB")
    private Blob logoData;

    public Team() {
    }

    public Team(String name, String stadiumName) {
        this.name = name;
        this.stadiumName = stadiumName;
        this.points = 0;
        this.playedMatchs = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    } 

    public int getPlayedMatchs() {
        return playedMatchs;
    }

    public void setPlayedMatchs(int playedMatchs) {
        this.playedMatchs = playedMatchs;
    }

    public Blob getLogoData() {
        return logoData;
    }

    public void setLogoData(Blob logoData) {
        this.logoData = logoData;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPlayedMatches() {
        return this.playedMatchs;
    }

    public void setPlayedMatches(int playedMatches) {
        this.playedMatchs = playedMatches;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
    public void updateStats(int goalsFor, int goalsAgainst) {
        this.playedMatchs++;
        if (goalsFor > goalsAgainst) {
            this.wins++;
            this.points += 3;
        } else if (goalsFor == goalsAgainst) {
            this.draws++;
            this.points += 1;
        } else {
            this.losses++;
        }
    }
}