package es.footleague.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    private String email;
    private String role; // "ADMIN", "JOURNALIST", "USER"
    private String avatarPath; // Ruta a la imagen del avatar

    @OneToMany(mappedBy = "author")
    private List<Rating> ratings;

    public Long getId() {
        return id;
    }

    public User(String username, String password, String email, String role, Team favoriteTeam) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.favoriteTeam = favoriteTeam;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
    @ManyToOne
    @JoinColumn(name = "favorite_team_id")
    private Team favoriteTeam;

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    // Añade su Getter y Setter
    public Team getFavoriteTeam() {
     return favoriteTeam;
    }

    public void setFavoriteTeam(Team favoriteTeam) {
        this.favoriteTeam = favoriteTeam;
    }
}
