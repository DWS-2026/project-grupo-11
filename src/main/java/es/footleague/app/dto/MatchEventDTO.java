package es.footleague.app.dto;

import es.footleague.app.model.MatchEvent;

public class MatchEventDTO {
    private Long id;
    private int minute;
    private String type;
    private String description;
    private String teamName;

    public MatchEventDTO(MatchEvent event) {
        this.id = event.getId();
        this.minute = event.getMinute();
        this.type = event.getType();
        this.description = event.getDescription(); // Reutilizamos tu lógica de descripción
        this.teamName = event.getTeam().getName();
    }
    // GETTERS
    public Long getId() { return id; }
    public int getMinute() { return minute; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getTeamName() { return teamName; }

    // SETTERS (Opcionales para Jackson, pero recomendados)
    public void setId(Long id) { this.id = id; }
    public void setMinute(int minute) { this.minute = minute; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
}