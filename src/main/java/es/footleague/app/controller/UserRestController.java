package es.footleague.app.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.footleague.app.dto.UserDTO;
import es.footleague.app.dto.UserMapper;
import es.footleague.app.dto.UserRegistrationDTO;
import es.footleague.app.model.User;
import es.footleague.app.security.jwt.AuthResponse;
import es.footleague.app.security.jwt.LoginRequest;
import es.footleague.app.security.jwt.UserLoginService;
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

    @Autowired
    private UserLoginService userLoginService;

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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return userLoginService.login(response, loginRequest);
    }

    // POST register
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO userDTO) {
        try {
            User newUser = userService.register(userDTO);
            return ResponseEntity.status(201).body(userMapper.toDTO(newUser));
        } catch (Exception e) {
            return ResponseEntity.status(409).build(); // Conflict (username exists)
        }
    }

    // POST logout
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        return userLoginService.logout(response);
    }

    // POST refresh
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            AuthResponse errorResponse = new AuthResponse(AuthResponse.Status.FAILURE, "Refresh token is missing");
            return ResponseEntity.status(401).body(errorResponse);
        }
        return userLoginService.refresh(response, refreshToken);
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
    @PreAuthorize("hasRole('ADMIN')")
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

    // PUT update user avatar (only owner or ADMIN)
    @PutMapping("/{id}/avatar")
    public ResponseEntity<Void> updateUserAvatar(@PathVariable Long id,
            @RequestParam MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Security: Only owner or ADMIN can update avatar
        User currentUser = userService.findByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow();
        boolean isOwner = currentUser.getId().equals(id);
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).build();
        }
        
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        userService.updateAvatar(id, imageFile.getBytes());
        return ResponseEntity.noContent().build();
    }
}
