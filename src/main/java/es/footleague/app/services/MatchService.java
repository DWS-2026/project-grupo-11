package es.footleague.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.footleague.app.model.Match;
import es.footleague.app.model.Rating;
import es.footleague.app.repository.MatchRepository;

@Service
public class MatchService {
    
    @Autowired
    private MatchRepository matchRepository;

    // Ahora devuelve una lista de partidos (Match), no de valoraciones
    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    // Guarda un objeto de tipo Match
    public void save(Match match) {
        matchRepository.save(match);
    }

    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }
    
    /**
     * Obtiene las valoraciones de un partido específico.
     * Basado en tu Match.java, accedemos a la lista de ratings interna.
     */
    public List<Rating> findRatingsByMatchId(Long matchId) {
        return matchRepository.findById(matchId)
                .map(Match::getRatings)
                .orElse(List.of()); // Devuelve lista vacía si el partido no existe
    }

    public void deleteById(long id) {
        matchRepository.deleteById(id);
    }
}