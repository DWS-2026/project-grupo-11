package es.footleague.app.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
    // Temporal

    @GetMapping("/400")
    public String view400() {
        return "error/400";
    } // Pase Impreciso

    @GetMapping("/403")
    public String view403() {
        return "error/403";
    } // Zona Exclusiva

    @GetMapping("/404")
    public String view404() {
        return "error/404";
    } // Fuera de Juego

    @GetMapping("/409")
    public String view409() {
        return "error/409";
    } // Jugada Repetida

    @GetMapping("/500")
    public String view500() {
        return "error/500";
    } // Tarjeta Roja

    @GetMapping("/503")
    public String view503() {
        return "error/503";
    } // En el Banquillo
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
                // Convertimos los bytes del archivo a un Blob
                user.setAvatarData(new javax.sql.rowset.serial.SerialBlob(imageFile.getBytes()));
            } catch (Exception e) {
                throw new IOException("Error al crear el blob del avatar", e);
            }
        }
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
            return "edit-profile";
        }
        return "user_not_found";
    }

    @PostMapping("/profile/edit")
    public String processEditProfile(User updatedUser) {
        // We look for the original user so as not to lose data that is not in the form
        // (like the role)
        Optional<User> userOpt = userService.findByUsernameIgnoreCase(updatedUser.getUsername());

        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();

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
            // Convertimos el flujo binario del Blob en un recurso descargable [cite: 238]
            Resource file = new InputStreamResource(image.getBinaryStream());

            // Detectamos automáticamente si es PNG, JPG, etc. [cite: 239, 240]
            MediaType mediaType = MediaTypeFactory.getMediaType(file).orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .contentType(mediaType) // Establecemos el tipo de contenido [cite: 244]
                    .body(file);
        }
        return ResponseEntity.notFound().build(); // Si no hay imagen, devuelve 404 [cite: 246]
    }

    @GetMapping("/team/{id}/logo")
    public ResponseEntity<Object> downloadLogo(@PathVariable long id) throws SQLException {
        Optional<Team> team = teamService.findById(id);

        if (team.isPresent() && team.get().getLogoData() != null) {
            Blob image = team.get().getLogoData();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // O usa MediaType.IMAGE_JPEG
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
