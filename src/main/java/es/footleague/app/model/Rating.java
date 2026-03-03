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
    private Integer score; 

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment; 

    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author; 

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private MatchEvent event;

    public Rating(Integer score, String comment, User author, MatchEvent event) {
        this.score = score;
        this.comment = comment;
        this.author = author;
        this.event = event;
        this.createdAt = LocalDateTime.now();
    }

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

    public MatchEvent getEvent() {
        return event;
    }

    public void setEvent(MatchEvent event) {
        this.event = event;
    }

}
