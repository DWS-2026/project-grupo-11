package es.footleague.app.services;

import es.footleague.app.model.User;
import es.footleague.app.dto.UserRegistrationDTO;
import es.footleague.app.dto.UserUpdateDTO;
import es.footleague.app.dto.PasswordChangeDTO;
import es.footleague.app.repository.TeamRepository;
import es.footleague.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private TeamRepository teamRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
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

    // Register a new user with validation
    public User register(UserRegistrationDTO dto) throws Exception {
        if (existsByUsername(dto.username())) {
            throw new Exception("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setEmail(dto.email());
        newUser.setPassword(dto.password()); // Will be encoded by save()
        newUser.setRoles(List.of("USER"));

        if (dto.favouriteTeamId() != null) {
            // Note: Could set favourite team here if TeamService is injected
            // teamService.findById(dto.favouriteTeamId()).ifPresent(newUser::setFavouriteTeam);
        }

        save(newUser);
        return newUser;
    }

    // Update user profile (email, favourite team) — NO PASSWORD
    @Transactional
    public User update(Long id, UserUpdateDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (dto.email() != null && !dto.email().trim().isEmpty()) {
            existingUser.setEmail(dto.email());
        }

        if (dto.favouriteTeamId() != null) {
            teamRepository.findById(dto.favouriteTeamId())
                    .ifPresent(existingUser::setFavouriteTeam);
        }

        userRepository.save(existingUser);
        return existingUser;
    }

    // Change password with validation of old password
    @Transactional
    public User changePassword(Long id, PasswordChangeDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Verify old password matches
        if (!passwordEncoder.matches(dto.oldPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Verify new password is not empty
        if (dto.newPassword() == null || dto.newPassword().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        // Update password (encode it)
        existingUser.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(existingUser);

        return existingUser;
    }
}