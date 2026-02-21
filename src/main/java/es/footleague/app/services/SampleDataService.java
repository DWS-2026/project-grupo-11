package es.footleague.app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void init(){
        //1. Crear equipos
        Team madridTeam = new Team("Real Madrid", "Santiago Bernabeu");
        Team barsaTeam = new Team("FC Barcelona", "Camp Nou");
        teamRepository.save(madridTeam);
        teamRepository.save(barsaTeam);
        //2. Crear usuarios
        User user1 = new User("Juan Perez", "password123", "juanperez@prensa.com", "PERIODISTA", madridTeam);
        User user2 = new User("admin", "admin123", "admin@footleague.es", "ADMIN", barsaTeam);
        userRepository.save(user1);
        userRepository.save(user2);
        //3. Crear partido
        Match match = new Match(madridTeam, barsaTeam, 2, 1, LocalDate.of(2026, 02, 01), LocalTime.of(21, 00));
        matchRepository.save(match);
        //4. Crear eventos
        MatchEvent goal1 = new MatchEvent("GOAL", 23, "Vinicius Jr.", match);
        MatchEvent card = new MatchEvent("YELLOW CARD", 40, "Gavi", match);
        MatchEvent goal2 = new MatchEvent("GOAL", 67, "Lewandowski", match);
        MatchEvent goal3 = new MatchEvent("GOAL", 89, "Bellingham", match);
        matchEventRepository.save(goal1);
        matchEventRepository.save(card);
        matchEventRepository.save(goal2);
        matchEventRepository.save(goal3);
        //5. Crear valoraciones
        Rating r1 = new Rating(5, "Espectacular remate de volea, totalmente imparable.", user1, match, goal3);
        Rating r2 = new Rating(2, "Demasiado rigurosa, apenas hubo contacto en la jugada.", user2, match, card);
        ratingRepository.save(r1);
        ratingRepository.save(r2);

        System.out.println("⚽ [SampleDataService] ¡Datos de FootLeague cargados con éxito!");
    }
}
