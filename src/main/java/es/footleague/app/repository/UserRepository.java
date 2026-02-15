package es.footleague.app.repository;

import es.footleague.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Aquí puedes añadir búsquedas personalizadas más adelante, como:
    // User findByUsername(String username);
}