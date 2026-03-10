package es.footleague.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.footleague.app.model.Match;
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
        if (match.getMatchDate() == null || match.getMatchTime() == null) {
            throw new IllegalArgumentException("El partido debe tener una fecha y una hora asignadas.");
        }
        matchRepository.save(match);
    }

    public Optional<Match> findFirst(){
        return matchRepository.findFirstByOrderByIdDesc();
    }

    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }

    public void deleteById(long id) {
        matchRepository.deleteById(id);
    }
}