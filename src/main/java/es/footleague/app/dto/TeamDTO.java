package es.footleague.app.dto; // Cambiado a un paquete de DTOs

import es.footleague.app.model.Team;

public class TeamDTO {
    private Long id;
    private String name;
    private String stadiumName;
    private int points;

    // Constructor necesario para el .map(TeamDTO::new) en el Controller
    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.stadiumName = team.getStadiumName();
        this.points = team.getPoints();
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStadiumName() { return stadiumName; }
    public int getPoints() { return points; }

    // Setters 
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStadiumName(String stadiumName) { this.stadiumName = stadiumName; }
    public void setPoints(int points) { this.points = points; }
}