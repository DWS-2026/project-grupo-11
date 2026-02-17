package es.footleague.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String logoPath; // Ruta a la imagen del escudo

    private Integer points = 0;
    private Integer goalsFor = 0;
    private Integer goalsAgainst = 0;
    
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
    public String getLogoPath() {
        return logoPath;
    }
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }
    public Integer getGoalsFor() {
        return goalsFor;
    }
    public void setGoalsFor(Integer goalsFor) {
        this.goalsFor = goalsFor;
    }
    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }
    public void setGoalsAgainst(Integer goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    
}
