package es.footleague.app.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.model.Rating;
import es.footleague.app.model.Team;
import es.footleague.app.model.User;
import es.footleague.app.repository.MatchEventRepository;
import es.footleague.app.repository.MatchRepository;
import es.footleague.app.repository.RatingRepository;
import es.footleague.app.repository.TeamRepository;
import es.footleague.app.repository.UserRepository;
import jakarta.annotation.PostConstruct;

@Service
public class SampleDataService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchEventRepository matchEventRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @PostConstruct
    public void init() throws IOException {
        // 1. Crear equipos
        Team madridTeam = new Team("Real Madrid", "Santiago Bernabeu");
        setTeamLogo(madridTeam, "static/img/logo_realMadrid.png");
        Team barsaTeam = new Team("FC Barcelona", "Camp Nou");
        setTeamLogo(barsaTeam, "static/img/logo_barcelona.jpg");
        barsaTeam.setPlayedMatches(1);
        madridTeam.setPlayedMatches(1);
        madridTeam.setWins(1);
        barsaTeam.setLosses(1);
        madridTeam.setPoints(3);
        teamRepository.save(madridTeam);
        teamRepository.save(barsaTeam);
        // 2. Crear usuarios
        User user1 = new User("JuanPerez", "password123", "juanperez@prensa.com", madridTeam);
        setUserAvatar(user1, "static/img/Juan_Perez_Avatar.PNG");
        User user2 = new User("admin", "admin123", "admin@footleague.es", barsaTeam);
        userRepository.save(user1);
        userRepository.save(user2);
        // 3. Crear partido
        Match match = new Match(madridTeam, barsaTeam, 2, 1, LocalDate.of(2026, 02, 01), LocalTime.of(21, 00));
        match.setWeather("Despejado");
        matchRepository.save(match);
        // 4. Crear eventos
        MatchEvent goal1 = new MatchEvent("GOAL", 23, "Vinicius Jr.", match, madridTeam);
        MatchEvent card = new MatchEvent("YELLOW_CARD", 40, "Gavi", match, barsaTeam);
        MatchEvent goal2 = new MatchEvent("GOAL", 67, "Lewandowski", match, barsaTeam);
        MatchEvent goal3 = new MatchEvent("GOAL", 89, "Bellingham", match, madridTeam);
        matchEventRepository.save(goal1);
        matchEventRepository.save(card);
        matchEventRepository.save(goal2);
        matchEventRepository.save(goal3);
        // 5. Crear valoraciones
        Rating r1 = new Rating(5, "Espectacular remate de volea, totalmente imparable.", user1, goal3);
        Rating r2 = new Rating(2, "Demasiado rigurosa, apenas hubo contacto en la jugada.", user2, card);
        ratingRepository.save(r1);
        ratingRepository.save(r2);

        System.out.println("⚽ [SampleDataService] ¡Datos de FootLeague cargados con éxito!");
    }

    private void setTeamLogo(Team team, String path) throws IOException {
        Resource image = new ClassPathResource(path);
        if (image.exists()) {
            team.setLogoData(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
            System.out.println("✅ Logo cargado para: " + team.getName());
        } else {
            System.out.println("❌ ERROR: No se encontró el archivo en: " + path);
        }
    }

    private void setUserAvatar(User user, String path) throws IOException {
        Resource image = new ClassPathResource(path);
        if (image.exists()) {
            user.setAvatarData(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
        }
    }
}
