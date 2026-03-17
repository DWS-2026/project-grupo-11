package es.footleague.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.footleague.app.model.Match;
import es.footleague.app.model.Team;
import es.footleague.app.repository.MatchRepository;
import es.footleague.app.repository.TeamRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    // Ahora devuelve una lista de partidos (Match), no de valoraciones
    public List<Match> findAll() {
        return matchRepository.findAll();
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
    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public void save(Match match) {
        if (match.getMatchDate() == null || match.getMatchTime() == null) {
            throw new IllegalArgumentException("El partido debe tener una fecha y una hora asignadas.");
        }

        // 1. Buscamos los equipos COMPLETOS en la base de datos usando los IDs que vienen del form
        Team local = teamRepository.findById(match.getLocalTeam().getId())
            .orElseThrow(() -> new RuntimeException("Equipo local no encontrado"));
        Team visitor = teamRepository.findById(match.getVisitorTeam().getId())
            .orElseThrow(() -> new RuntimeException("Equipo visitante no encontrado"));

        // 2. Asignamos estos equipos completos al objeto match
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);

        // 3. Guardamos el partido
        matchRepository.save(match);

        // 4. Ahora sí, actualizamos las estadísticas sobre los equipos que tienen nombre y datos
        if (match.getLocalGoals() != null && match.getVisitorGoals() != null) {
            local.updateStats(match.getLocalGoals(), match.getVisitorGoals());
            visitor.updateStats(match.getVisitorGoals(), match.getLocalGoals());
    
            // 5. Guardamos los equipos (ahora Hibernate no se quejará porque el 'name' está presente)
            teamRepository.save(local);
            teamRepository.save(visitor);
        }
    }
}