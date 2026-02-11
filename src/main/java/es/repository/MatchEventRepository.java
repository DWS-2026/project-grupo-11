package main.java.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.model.MatchEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    // Aquí ya tienes métodos como save(), findAll(), deleteById(), etc.
}
