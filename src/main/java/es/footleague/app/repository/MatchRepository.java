package es.footleague.app.repository;

import es.footleague.app.model.Match;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findFirstByOrderByIdDesc();
}
