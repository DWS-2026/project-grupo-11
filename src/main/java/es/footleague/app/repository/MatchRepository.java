package es.footleague.app.repository;

import es.footleague.app.model.Match;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findFirstByOrderByIdDesc();

    // This method counts how many matches there are where the given team ID is either the local team or the visitor team. This can be useful to check if a team can be deleted (if it has matches, it cannot be deleted).
    long countByLocalTeamIdOrVisitorTeamId(Long localId, Long visitorId);
}
