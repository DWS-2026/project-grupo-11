package es.footleague.app.services;

import es.footleague.app.model.User;
import es.footleague.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Blob;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    @Transactional
    public void deleteByUsername(String username) {
        userRepository.findByUsernameIgnoreCase(username)
                .ifPresent(userRepository::delete);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateAvatar(Long id, byte[] avatarBytes) throws Exception {
        User user = userRepository.findById(id).orElseThrow();
        user.setAvatarData(new javax.sql.rowset.serial.SerialBlob(avatarBytes));
        userRepository.save(user);
    }

    public Optional<Blob> getAvatar(Long id) {
        return userRepository.findById(id).map(User::getAvatarData);
    }
}