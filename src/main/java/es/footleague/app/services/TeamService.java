package es.footleague.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.footleague.app.model.Team;
import es.footleague.app.repository.MatchRepository;
import es.footleague.app.repository.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; // Opcional, para valores por defecto

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    /**
     * It retrieves all teams from the database and returns them as a list. This is useful for displaying all teams in the application, such as in a list or table format.
     */
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    /**
     * It retrieves a team from the database by its ID and returns it as an Optional.
     */
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return teamRepository.existsById(id);
    }

    public List<Team> findAllOrderByPoints() {
        return teamRepository.findAllByOrderByPointsDesc();
    }

    public boolean canDelete(Long teamId) {
        // If the count of matches where the team is either local or visitor is greater than 0, 
        // it means that the team has played matches and therefore cannot be deleted.
        //  If the count is 0, it means that the team has not played any matches and can be safely 
        // deleted without affecting any match records.
        return matchRepository.countByLocalTeamIdOrVisitorTeamId(teamId, teamId) == 0;
    }

    /**
     * It saves a team to the database.
     * This is useful for creating a new team or updating an existing one.
     */
    public Team save(Team team) { // Changed from void to Team
        return teamRepository.save(team); // Added return
    }

    /**
     * It deletes a team from the database by its ID.
     */
    public void deleteById(Long id) {
        if (teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
        }
    }
    public Page<Team> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }
}