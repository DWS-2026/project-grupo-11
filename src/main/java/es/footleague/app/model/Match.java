package es.footleague.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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

    protected Match(){}
    
    public Match(Team localTeam, Team visitorTeam, Integer localGoals, Integer visitorGoals, LocalDateTime matchDate) {
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.localGoals = localGoals;
        this.visitorGoals = visitorGoals;
        this.matchDate = matchDate;
    }

    private LocalDateTime matchDate;

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
    }

    public Team getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(Team visitorTeam) {
        this.visitorTeam = visitorTeam;
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

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
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

    
}
