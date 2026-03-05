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
    private int goals;

    @Lob
    @Column(name = "logo_data", columnDefinition = "LONGBLOB")
    private byte[] logoData;

    // --- CAMBIO AQUÍ: De protected a public ---
    public Team() {
        this.points = 0;
        this.goals = 0;
    }

    public Team(String name, String stadiumName) {
        this.name = name;
        this.stadiumName = stadiumName;
        this.points = 0;
        this.goals = 0;
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStadiumName() { return stadiumName; }
    public void setStadiumName(String stadiumName) { this.stadiumName = stadiumName; }
    public byte[] getLogoData() { return logoData; }
    public void setLogoData(byte[] logoData) { this.logoData = logoData; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }
}