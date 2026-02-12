package main.java.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.model.Match;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    // Aquí ya tienes métodos como save(), findAll(), deleteById(), etc.
}
