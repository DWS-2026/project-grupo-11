package es.footleague.app.model;

import jakarta.persistence.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Column(unique = true, nullable = false)
    private String email;

    @Lob
    @Column(name = "avatar_data", columnDefinition = "LONGBLOB")
    private Blob avatarData;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "favourite_team_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Team favouriteTeam;

    public User() {
    }

    public User(String username, String password, String email, Team favouriteTeam) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.favouriteTeam = favouriteTeam;
    }

    public Long getId() {
        return id;
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

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Team getFavouriteTeam() {
        return favouriteTeam;
    }

    public void setFavouriteTeam(Team favouriteTeam) {
        this.favouriteTeam = favouriteTeam;
    }

    public Blob getAvatarData() {
        return avatarData;
    }

    public void setAvatarData(Blob avatarData) {
        this.avatarData = avatarData;
    }

}
