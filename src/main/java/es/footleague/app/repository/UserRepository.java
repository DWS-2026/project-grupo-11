package es.footleague.app.repository;

import es.footleague.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // This method allows you to find a user by their username, 
    // ignoring case sensitivity. This is useful for login and 
    // other operations where you want to retrieve a user based 
    // on their username without worrying about the case.
    Optional<User> findByUsernameIgnoreCase(String username);
}