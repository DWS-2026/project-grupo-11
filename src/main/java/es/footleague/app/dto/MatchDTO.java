package es.footleague.app.dto;

import es.footleague.app.model.Match;
import java.util.List;
import java.util.stream.Collectors;

public class MatchDTO {
    private Long id;
    private String localTeamName;
    private String visitorTeamName;
    private Integer localGoals;
    private Integer visitorGoals;
    private String stadium;
    private String date;
    private List<MatchEventDTO> events;

    public MatchDTO(Match match) {
        this.id = match.getId();
        this.localTeamName = match.getLocalTeam().getName();
        this.visitorTeamName = match.getVisitorTeam().getName();
        this.localGoals = match.getLocalGoals();
        this.visitorGoals = match.getVisitorGoals();
        this.stadium = match.getStadium();
        this.date = match.getMatchDate().toString() + " " + match.getMatchTime().toString();
        this.events = match.getEvents().stream()
                .map(event -> new MatchEventDTO(
                    event.getId(),
                    event.getMinute(),          // Verifica que este método exista en MatchEvent
                    event.getType(),       // O event.getType() según tu modelo
                    event.getNamePlayer(),      // Ajusta según los nombres en tu entidad
                    event.getNamePlayerOut(),   // Ajusta según los nombres en tu entidad
                    event.getNamePlayerIn(),    // Ajusta según los nombres en tu entidad
                    event.getMatch().getId(),
                    event.getTeam().getId(),
                    event.getTeam().getName()
                ))
                .collect(Collectors.toList());
    }
    // GETTERS
    public Long getId() { return id; }
    public String getLocalTeamName() { return localTeamName; }
    public String getVisitorTeamName() { return visitorTeamName; }
    public Integer getLocalGoals() { return localGoals; }
    public Integer getVisitorGoals() { return visitorGoals; }
    public String getStadium() { return stadium; }
    public String getDate() { return date; }
    public List<MatchEventDTO> getEvents() { return events; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setLocalTeamName(String localTeamName) { this.localTeamName = localTeamName; }
    public void setVisitorTeamName(String visitorTeamName) { this.visitorTeamName = visitorTeamName; }
    public void setLocalGoals(Integer localGoals) { this.localGoals = localGoals; }
    public void setVisitorGoals(Integer visitorGoals) { this.visitorGoals = visitorGoals; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public void setDate(String date) { this.date = date; }
    public void setEvents(List<MatchEventDTO> events) { this.events = events; }
}