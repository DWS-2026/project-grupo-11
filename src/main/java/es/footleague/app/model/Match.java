package es.footleague.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "local_team_id")
    private Team localTeam;

    @ManyToOne
    @JoinColumn(name = "visitor_team_id")
    private Team visitorTeam;

    private Integer localGoals;
    private Integer visitorGoals;
    private String weather;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    protected Match() {
    }

    public Match(Team localTeam, Team visitorTeam, Integer localGoals, Integer visitorGoals, LocalDate matchDate, LocalTime matchTime) {
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.localGoals = localGoals;
        this.visitorGoals = visitorGoals;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
    }

    private LocalDate matchDate;
    private LocalTime matchTime;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private List<MatchEvent> events;

    @OneToMany(mappedBy = "match")
    private List<Rating> ratings;

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
        this.visitorTeam = visitorTeam;
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

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public boolean isFinalized() {
    if (this.matchDate == null || this.matchTime == null) return false;
    
    LocalDateTime fullDateTime = LocalDateTime.of(this.matchDate, this.matchTime);
    return fullDateTime.isBefore(LocalDateTime.now());
    }
}
