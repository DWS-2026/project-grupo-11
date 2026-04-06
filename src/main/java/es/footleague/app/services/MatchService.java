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

    public Optional<Match> findFirst() {
        return matchRepository.findFirstByOrderByIdDesc();
    }

    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        matchRepository.findById(id).ifPresent(match -> {
            // 1. Buscamos los equipos para restarles las estadísticas
            Team local = match.getLocalTeam();
            Team visitor = match.getVisitorTeam();

            // 2. Restamos el partido jugado y el resultado
            revertTeamStats(match, local, visitor);

            // 3. Guardamos los equipos limpios
            teamRepository.save(local);
            teamRepository.save(visitor);

            // 4. Borramos el partido
            matchRepository.delete(match);
        });
    }

    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public void save(Match match) {
        if (match.getMatchDate() == null || match.getMatchTime() == null) {
            throw new IllegalArgumentException("El partido debe tener una fecha y una hora asignadas.");
        }

        // 1. Buscamos los equipos COMPLETOS en la base de datos usando los IDs que
        // vienen del form
        Team local = teamRepository.findById(match.getLocalTeam().getId())
                .orElseThrow(() -> new RuntimeException("Equipo local no encontrado"));
        Team visitor = teamRepository.findById(match.getVisitorTeam().getId())
                .orElseThrow(() -> new RuntimeException("Equipo visitante no encontrado"));

        // 2. Detectar si es EDICIÓN
        if (match.getId() != null) {
            Match oldMatch = matchRepository.findById(match.getId())
                    .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

            // REVERTIMOS estadísticas del resultado anterior antes de aplicar el nuevo
            // OJO: No restamos 'playedMatchs' porque el partido se sigue jugando, solo
            // cambian los goles
            revertOldResultOnly(oldMatch);
        } else {
            // 3. ¿Es NUEVO?
            // Solo sumamos +1 aquí y nos aseguramos de que NADIE más lo sume.
            local.setPlayedMatchs(local.getPlayedMatchs() + 1);
            visitor.setPlayedMatchs(visitor.getPlayedMatchs() + 1);
        }

        // 4. Asignamos estos equipos completos al objeto match
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);

        // 5. Guardamos el partido
        matchRepository.save(match);

        // 6. Ahora sí, actualizamos las estadísticas sobre los equipos que tienen
        // nombre y datos
        if (match.getLocalGoals() != null && match.getVisitorGoals() != null) {
            local.updateStats(match.getLocalGoals(), match.getVisitorGoals());
            visitor.updateStats(match.getVisitorGoals(), match.getLocalGoals());

            // 7. Guardamos los equipos (ahora Hibernate no se quejará porque el 'name' está
            // presente)
            teamRepository.save(local);
            teamRepository.save(visitor);
        }
    }

    // MÉTODO AUXILIAR: Revierte puntos, victorias, etc., pero NO los partidos
    // jugados
    private void revertOldResultOnly(Match m) {
        Team local = m.getLocalTeam();
        Team visitor = m.getVisitorTeam();

        if (m.getLocalGoals() > m.getVisitorGoals()) { // Ganó local
            local.setWins(local.getWins() - 1);
            local.setPoints(local.getPoints() - 3);
            visitor.setLosses(visitor.getLosses() - 1);
        } else if (m.getLocalGoals() < m.getVisitorGoals()) { // Ganó visitante
            visitor.setWins(visitor.getWins() - 1);
            visitor.setPoints(visitor.getPoints() - 3);
            local.setLosses(local.getLosses() - 1);
        } else { // Empate
            local.setDraws(local.getDraws() - 1);
            local.setPoints(local.getPoints() - 1);
            visitor.setDraws(visitor.getDraws() - 1);
            visitor.setPoints(visitor.getPoints() - 1);
        }
    }

    // MÉTODO AUXILIAR: Revierte TODO (se usa al borrar el partido)
    private void revertTeamStats(Match m, Team local, Team visitor) {
        local.setPlayedMatchs(local.getPlayedMatchs() - 1);
        visitor.setPlayedMatchs(visitor.getPlayedMatchs() - 1);
        revertOldResultOnly(m);
    }
}