package es.footleague.app.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.footleague.app.dto.UserDTO;
import es.footleague.app.dto.UserMapper;
import es.footleague.app.dto.UserRegistrationDTO;
import es.footleague.app.model.User;
import es.footleague.app.services.TeamService;
import es.footleague.app.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeamService teamService;

    // Get current logged-in user
    @GetMapping("/me")
    public UserDTO me(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByUsernameIgnoreCase(principal.getName())
                    .orElseThrow(NoSuchElementException::new);
            return userMapper.toDTO(user);
        } else {
            throw new NoSuchElementException();
        }
    }

    // POST login
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    // POST register
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO userDTO) {
        // Check if username already exists
        if (userService.existsByUsername(userDTO.username())) {
            return ResponseEntity.status(409).build(); // Conflict
        }

        User newUser = new User();
        newUser.setUsername(userDTO.username());
        newUser.setEmail(userDTO.email());
        newUser.setPassword(userDTO.username()); // Se sobreescribe abajo
        newUser.setRoles(List.of("USER"));

        // Set favourite team if provided
        if (userDTO.favouriteTeamId() != null) {
            teamService.findById(userDTO.favouriteTeamId())
                    .ifPresent(newUser::setFavouriteTeam);
        }

        userService.save(newUser);

        return ResponseEntity.status(201).body(userMapper.toDTO(newUser));
    }

    // Get all users — ADMIN only, paginated
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.findAll(pageable)
                .map(userMapper::toDTO);
        return ResponseEntity.ok(users);
    }

    // Get user by id — ADMIN only
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete user by id — ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET user avatar
    @GetMapping("/{id}/avatar")
    public ResponseEntity<Object> getUserAvatar(@PathVariable Long id) throws SQLException {
        User user = userService.findById(id).orElseThrow();
        if (user.getAvatarData() == null) {
            return ResponseEntity.notFound().build();
        }
        var avatar = user.getAvatarData();
        var resource = new InputStreamResource(avatar.getBinaryStream());
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    // PUT update user avatar
    @PutMapping("/{id}/avatar")
    public ResponseEntity<Void> updateUserAvatar(@PathVariable Long id,
            @RequestParam MultipartFile imageFile) throws Exception {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        userService.updateAvatar(id, imageFile.getBytes());
        return ResponseEntity.noContent().build();
    }
}
