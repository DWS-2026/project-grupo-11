package es.footleague.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String logoPath; // Ruta a la imagen del escudo
    private String stadiumName;
    
    public Team(String name, String stadiumName) {
        this.name = name;
        this.stadiumName = stadiumName;
    }
    public Long getId() {
        return id;
    }
    public String getStadiumName() {
        return stadiumName;
    }
    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
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
    public String getLogoPath() {
        return logoPath;
    }
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
    
}
