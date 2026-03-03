package es.footleague.app.services;

import es.footleague.app.model.User;
import es.footleague.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    public void deleteByUsername(String username) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        user.ifPresent(usuario -> userRepository.delete(usuario));
    }

    public void save(User user) {
        // Aquí podrías poner lógica para cifrar la contraseña antes de guardar
        userRepository.save(user);
    }
}
