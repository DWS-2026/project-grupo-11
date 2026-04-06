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

    // This method retrieves all matches from the database and returns them as a list. It uses the findAll() method provided by the MatchRepository, which is a Spring Data JPA repository that extends JpaRepository. This allows you to easily perform CRUD operations on the Match entity without having to write complex SQL queries.
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
            // 1. We get the local and visitor teams of the match to be deleted, because we need to update their statistics (matches played, wins, losses, etc.) before deleting the match.
            Team local = match.getLocalTeam();
            Team visitor = match.getVisitorTeam();

            // 2. We revert the statistics of both teams as if the match had never been played. This means we will decrease the number of matches played by both teams and also revert the wins, losses, draws and points according to the result of the match that is being deleted.
            revertTeamStats(match, local, visitor);

            // 3. We save the updated teams to the database so that the changes in their statistics are persisted before we delete the match. This is important because if we delete the match first, we would lose the information about which teams were involved and what the result was, making it impossible to revert their statistics correctly.
            teamRepository.save(local);
            teamRepository.save(visitor);

            // 4. Finally, we delete the match from the database using the delete() method of the MatchRepository. This will remove the match record from the database, and since we have already updated the teams' statistics, everything will be consistent.
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

        // 1. We retrieve the full Team objects from the database based on the IDs that come in the 'match' object. This is necessary because the 'match' object that comes from the form may only have the team IDs, and we need to load the complete Team entities to work with them and update their statistics correctly.
        Team local = teamRepository.findById(match.getLocalTeam().getId())
                .orElseThrow(() -> new RuntimeException("Equipo local no encontrado"));
        Team visitor = teamRepository.findById(match.getVisitorTeam().getId())
                .orElseThrow(() -> new RuntimeException("Equipo visitante no encontrado"));

        // 2. Obtener estado anterior del partido (si existe)
        Match oldMatch = null;

        if (match.getId() != null) {
            oldMatch = matchRepository.findById(match.getId())
                    .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        }

        boolean wasPlayed = oldMatch != null && oldMatch.isPlayed();
        boolean isNowPlayed = match.isPlayed();

        // 3. TRANSICIONES DE ESTADO

        // 🟢 Caso 1: NO jugado → jugado
        if (!wasPlayed && isNowPlayed) {
            local.setPlayedMatchs(local.getPlayedMatchs() + 1);
            visitor.setPlayedMatchs(visitor.getPlayedMatchs() + 1);
        }

        // 🔴 Caso 2: YA jugado → sigue jugado (edición)
        if (wasPlayed && isNowPlayed) {
            revertOldResultOnly(oldMatch);
        }

        // ⚫ Caso 3: jugado → NO jugado (raro, pero controlado)
        if (wasPlayed && !isNowPlayed) {
            revertTeamStats(oldMatch, local, visitor);
        }

        // 🔴 Caso 2: YA jugado → sigue jugado (edición)
        if (wasPlayed && isNowPlayed) {
            revertOldResultOnly(oldMatch);
        }

        // ⚫ Caso 3: jugado → NO jugado (raro, pero controlado)
        if (wasPlayed && !isNowPlayed) {
            revertTeamStats(oldMatch, local, visitor);
        }

        // 4. We set the local and visitor teams in the match object to ensure that the relationship is properly established before saving the match. This is important because the match needs to have references to the full Team entities (not just their IDs) in order for Hibernate to manage the relationships and for the statistics updates to work correctly.
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);

        // 5. We save the match
        matchRepository.save(match);

        // 6. Aplicar estadísticas SOLO si está jugado
        if (isNowPlayed && match.getLocalGoals() != null && match.getVisitorGoals() != null) {

            local.updateStats(match.getLocalGoals(), match.getVisitorGoals());
            visitor.updateStats(match.getVisitorGoals(), match.getLocalGoals());

            // 7. Guardamos los equipos (ahora Hibernate no se quejará porque el 'name' está
            // presente)
            teamRepository.save(local);
            teamRepository.save(visitor);
        }
    }

    // Aux Method to revert only the result of a match (used when editing a match, to revert the old result before applying the new one). It does not decrease the number of matches played, only reverts the wins, losses, draws and points according to the old result of the match that is being edited. This way we can ensure that when we edit a match, we do not accidentally decrease the matches played for the teams, which would be incorrect since the match still exists and is still being played, we are just changing its result.
    private void revertOldResultOnly(Match m) {
        Team local = m.getLocalTeam();
        Team visitor = m.getVisitorTeam();

        if (m.getLocalGoals() > m.getVisitorGoals()) { // Local wins
            local.setWins(local.getWins() - 1);
            local.setPoints(local.getPoints() - 3);
            visitor.setLosses(visitor.getLosses() - 1);
        } else if (m.getLocalGoals() < m.getVisitorGoals()) { // Visitor wins
            visitor.setWins(visitor.getWins() - 1);
            visitor.setPoints(visitor.getPoints() - 3);
            local.setLosses(local.getLosses() - 1);
        } else { // Draw
            local.setDraws(local.getDraws() - 1);
            local.setPoints(local.getPoints() - 1);
            visitor.setDraws(visitor.getDraws() - 1);
            visitor.setPoints(visitor.getPoints() - 1);
        }
    }

    // Aux Method to revert all statistics of a match (used when deleting a match)
    private void revertTeamStats(Match m, Team local, Team visitor) {
        local.setPlayedMatchs(local.getPlayedMatchs() - 1);
        visitor.setPlayedMatchs(visitor.getPlayedMatchs() - 1);
        revertOldResultOnly(m);
    }
}