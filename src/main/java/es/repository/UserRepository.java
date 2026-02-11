package main.java.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Aquí ya tienes métodos como save(), findAll(), deleteById(), etc.
}
