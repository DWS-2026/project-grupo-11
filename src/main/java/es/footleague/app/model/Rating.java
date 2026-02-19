package es.footleague.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer score; // Nota de 1 a 5, por ejemplo

    @Column(columnDefinition = "TEXT")
    private String comment; // Opinión del periodista

    private LocalDateTime createdAt;

    
    public Rating(Integer score, String comment, User author, Match match, MatchEvent event) {
        this.score = score;
        this.comment = comment;
        this.author = author;
        this.match = match;
        this.event = event;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author; // El periodista que valora

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match; // El partido valorado

    @ManyToOne
    private MatchEvent event;

    // Constructor, Getters y Setters
    public Rating() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public MatchEvent getEvent() {
        return event;
    }

    public void setEvent(MatchEvent event) {
        this.event = event;
    }

}
