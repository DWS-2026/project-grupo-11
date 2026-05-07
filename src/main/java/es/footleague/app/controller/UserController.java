package es.footleague.app.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.model.Team;
import es.footleague.app.model.User;
import es.footleague.app.services.FileStorageService;
import es.footleague.app.services.MatchService;
import es.footleague.app.services.TeamService;
import es.footleague.app.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private FileStorageService fileStorageService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<User> user = userService.findByUsernameIgnoreCase(principal.getName());
            if (user.isPresent()) {
                model.addAttribute("loggedUser", user.get());
                model.addAttribute("logged", true);
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
            }
        } else {
            model.addAttribute("logged", false);
        }
    }

    // 1. PROFILE VIEW (To view user data)
    // We use the username because it is unique, as you defined in the entity
    @GetMapping("/profile/{username}")
    public String userProfile(@PathVariable String username, Model model, HttpServletRequest request) {
        Optional<User> user = userService.findByUsernameIgnoreCase(username);

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            Principal principal = request.getUserPrincipal();
            boolean isOwner = principal != null && principal.getName().equalsIgnoreCase(username);
            model.addAttribute("isOwner", isOwner);
            return "profile";
        }
        return "user_not_found";
    }

    @GetMapping("/400")
    public String view400() {
        return "error/400";
    } // Bad Request

    @GetMapping("/403")
    public String view403() {
        return "error/403";
    } // Restricted Access

    @GetMapping("/404")
    public String view404() {
        return "error/404";
    } // Not Found

    @GetMapping("/409")
    public String view409() {
        return "error/409";
    } // Conflict (e.g., username already exists)

    @GetMapping("/500")
    public String view500() {
        return "error/500";
    } // Internal Server Error

    @GetMapping("/503")
    public String view503() {
        return "error/503";
    } // Service Unavailable (e.g., database down)
      //
      // 2. REGISTRATION FORM (View)

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("teams", teamService.findAll());
        return "registration";
    }

    // 3. PROCESS RECORD (Logic)
    @PostMapping("/register")
    public String processRegister(User user, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            try {
                fileStorageService.validateImageFile(imageFile);
                // We convert the uploaded file into a Blob to store in MySQL [cite: 73]
                user.setAvatarData(new javax.sql.rowset.serial.SerialBlob(imageFile.getBytes()));
            } catch (Exception e) {
                throw new IOException("Error al crear el blob del avatar", e);
            }
        }
        user.setRoles(List.of("USER"));
        userService.save(user);
        return "redirect:/profile/" + user.getUsername();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile/{username}/my-ratings")
    public String myRatings(@PathVariable String username, Model model) {
        Optional<User> user = userService.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("ratings", user.get().getRatings());
            return "my-ratings";
        }
        return "user_not_found";
    }

    @GetMapping("/profile/{username}/edit")
    public String editProfileForm(@PathVariable String username, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        boolean isOwner = principal != null && principal.getName().equalsIgnoreCase(username);
        boolean isAdmin = request.isUserInRole("ADMIN");

        if (principal == null || !(isOwner || isAdmin)) {
            return "redirect:/profile/" + username;
        }

        Optional<User> user = userService.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("teams", teamService.findAll());
            return "edit-profile";
        }
        return "user_not_found";
    }

    @PostMapping("/profile/edit")
    public String processEditProfile(User updatedUser, @RequestParam("imageFile") MultipartFile imageFile)
            throws IOException {
        // We look for the original user so as not to lose data that is not in the form
        // (like the role)
        Optional<User> userOpt = userService.findByUsernameIgnoreCase(updatedUser.getUsername());

        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();

            if (!imageFile.isEmpty()) {
                try {
                    fileStorageService.validateImageFile(imageFile);
                    existingUser.setAvatarData(new javax.sql.rowset.serial.SerialBlob(imageFile.getBytes()));
                } catch (Exception e) {
                    throw new IOException("Error al actualizar el avatar", e);
                }
            }

            // 2. Logic for Email: We only update if a new one has been sent and
            // it is not blank
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
                existingUser.setEmail(updatedUser.getEmail());
            }

            existingUser.setFavouriteTeam(updatedUser.getFavouriteTeam());

            // We only change the password if you've entered something new
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(updatedUser.getPassword());
            }
            userService.save(existingUser);
        }

        return "redirect:/profile/" + updatedUser.getUsername();
    }

    @GetMapping("/user/{username}/avatar")
    public ResponseEntity<Object> downloadAvatar(@PathVariable String username) throws SQLException {
        Optional<User> user = userService.findByUsernameIgnoreCase(username);
        if (user.isPresent() && user.get().getAvatarData() != null) {
            Blob image = user.get().getAvatarData();
            // We create a downloadable resource from the binary stream [cite: 238]
            Resource file = new InputStreamResource(image.getBinaryStream());

            // We detect the media type automatically (PNG, JPG, etc.) [cite: 239, 240]
            MediaType mediaType = MediaTypeFactory.getMediaType(file).orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .contentType(mediaType) // We set the content type [cite: 244]
                    .body(file);
        }
        return ResponseEntity.notFound().build(); // If there is no image, return 404 [cite: 246]
    }

    @GetMapping("/team/{id}/logo")
    public ResponseEntity<Object> downloadLogo(@PathVariable long id) throws Exception {
        Optional<Team> teamOpt = teamService.findById(id);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Team team = teamOpt.get();
        if (team.getLogoFilePath() != null) {
            Resource resource = fileStorageService.loadFileAsResource(team.getLogoFilePath());
            MediaType mediaType = MediaTypeFactory.getMediaType(team.getLogoFileName())
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + team.getLogoFileName() + "\"")
                    .body(resource);
        }

        if (team.getLogoData() != null) {
            Blob image = team.getLogoData();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // Or it use MediaType.IMAGE_JPEG
                    .body(file);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("teams", teamService.findAllOrderByPoints());
        Optional<Match> featuredMatchOpt = matchService.findFirst();
        if (featuredMatchOpt.isPresent()) {
            Match featuredMatch = featuredMatchOpt.get();
            model.addAttribute("match", featuredMatch);
        }
        return "index";
    }

    @GetMapping("/classification")
    public String classification(Model model) {
        model.addAttribute("teams", teamService.findAllOrderByPoints());
        return "classification";
    }

    @GetMapping("/match-list")
    public String showMatchList(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "match-list";
    }

    @GetMapping("/match/{id}")
    public String matchDetail(@PathVariable Long id, Model model) {
        Optional<Match> matchOpt = matchService.findById(id);

        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("events", match.getEvents());
            model.addAttribute("teams", teamService.findAll());

            model.addAttribute("newEvent", new MatchEvent());

            return "match-details";
        }
        return "match-not-found";
    }
}
