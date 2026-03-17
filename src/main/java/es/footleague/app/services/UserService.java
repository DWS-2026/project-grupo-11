package es.footleague.app.services;

import es.footleague.app.model.User;
import es.footleague.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                roles);
    }

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
