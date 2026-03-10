package es.footleague.app.model;

import jakarta.persistence.*;
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
    private byte[] logoData;

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

    /**
     * Helper para Mustache
     */
    public String getLogoBase64() {
        if (this.logoData != null && this.logoData.length > 0) {
            return Base64.getEncoder().encodeToString(this.logoData);
        }
        return null;
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

    public byte[] getLogoData() {
        return logoData;
    }

    public void setLogoData(byte[] logoData) {
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
}