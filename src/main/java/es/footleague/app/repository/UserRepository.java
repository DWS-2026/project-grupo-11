package es.footleague.app.repository;

import es.footleague.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Este método es fundamental para el Login
    Optional<User> findByUsername(String username);
}