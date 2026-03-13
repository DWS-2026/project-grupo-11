package es.footleague.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_team_id", nullable = false)
    private Team localTeam;

    @ManyToOne(optional = false)
    @JoinColumn(name = "visitor_team_id", nullable = false)
    private Team visitorTeam;

    @Column(nullable = false)
    private Integer localGoals;

    @Column(nullable = false)
    private Integer visitorGoals;

    private String weather;

    @Column(nullable = false)
    private String stadium;

    @Column(nullable = false)
    private LocalDate matchDate;

    @Column(nullable = false)
    private LocalTime matchTime;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchEvent> events = new ArrayList<>();

    public Match() {
    }

    public Match(Team localTeam, Team visitorTeam, Integer localGoals, Integer visitorGoals, LocalDate matchDate,
            LocalTime matchTime) {
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.localGoals = localGoals;
        this.visitorGoals = visitorGoals;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
        this.stadium = localTeam.getStadiumName();
    }

    public boolean getClimaDespejado() { return "Despejado".equalsIgnoreCase(weather); }
    public boolean getClimaNublado() { return "Nublado".equalsIgnoreCase(weather); }
    public boolean getClimaLluvia() { return "Lluvia".equalsIgnoreCase(weather); }
    public boolean getClimaNieve() {return "Nieve".equalsIgnoreCase(this.weather);}
    public boolean getClimaViento() {return "Viento".equalsIgnoreCase(this.weather);}
    

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getLocalTeam() {
        return localTeam;
    }

    public void setLocalTeam(Team localTeam) {
        this.localTeam = localTeam;
    }

    public Team getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(Team visitorTeam) {
        this.visitorTeam = visitorTeam;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public Integer getLocalGoals() {
        return localGoals;
    }

    public void setLocalGoals(Integer localGoals) {
        this.localGoals = localGoals;
    }

    public Integer getVisitorGoals() {
        return visitorGoals;
    }

    public void setVisitorGoals(Integer visitorGoals) {
        this.visitorGoals = visitorGoals;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public LocalTime getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(LocalTime matchTime) {
        this.matchTime = matchTime;
    }

    public List<MatchEvent> getEvents() {
        return events;
    }

    public void setEvents(List<MatchEvent> events) {
        this.events = events;
    }

    public boolean isFinalized() {
        if (this.matchDate == null || this.matchTime == null)
            return false;

        LocalDateTime fullDateTime = LocalDateTime.of(this.matchDate, this.matchTime);
        return fullDateTime.isBefore(LocalDateTime.now());
    }
}
