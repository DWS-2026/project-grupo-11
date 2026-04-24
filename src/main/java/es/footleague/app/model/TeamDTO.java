package es.footleague.app.model;

import es.footleague.app.model.Team;

public class TeamDTO {
    private Long id;
    private String name;
    private String stadiumName;
    private int points;

    // Constructor para convertir de Entidad a DTO
    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.stadiumName = team.getStadiumName();
        this.points = team.getPoints();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStadiumName() { return stadiumName; }
    public int getPoints() { return points; }
}