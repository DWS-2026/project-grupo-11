package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.model.User;
import es.footleague.app.services.MatchService;
import es.footleague.app.services.TeamService;
import es.footleague.app.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;
import java.security.Principal;
import java.util.HashMap;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<User> user = userService.findByUsernameIgnoreCase(principal.getName());
            if (user.isPresent()) {
                model.addAttribute("loggedUser", user.get());
                model.addAttribute("logged", true);
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
                // The token is added automatically by your CSRFHandlerInterceptor
            }
        }
    }

    @GetMapping("/modify-match")
    public String adminListMatches(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "ModifyMatch";
    }

    @GetMapping("/match-create")
    public String showCreateForm(Model model) {
        model.addAttribute("match", new Match());
        model.addAttribute("teams", teamService.findAll());
        return "CreateMatch";
    }

    @GetMapping("/match-edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Match> matchOpt = matchService.findById(id);
        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("teams", teamService.findAll());
            // Weather attribute for the HTML, e.g. climaSoleado, climaLluvioso, etc.
            // depending
            // on match.getWeather()
            // HTML
            model.addAttribute("clima" + match.getWeather(), true);
            model.addAttribute("events", match.getEvents());
            return "EditMatchDetails";
        }
        return "redirect:/admin/matches/modify-match";
    }

    @PostMapping("/match/save")
    @ResponseBody
    public ResponseEntity<?> saveMatch(@ModelAttribute Match match) {
        try {
            // Prepare the match (set stadium, link events, etc.)
            matchService.prepareMatchForSave(match);

            // Save the match (which will also save the events due to cascade settings)
            matchService.save(match);

            // Return a success response with a message
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Partido guardado correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error saving match", e); // Log con contexto
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error",
                            "message", "An unexpected error occurred")); // Genérico
        }
    }

    @PostMapping("/match-delete/{id}")
    public String deleteMatch(@PathVariable Long id) {
        if (matchService.findById(id).isEmpty()) {
            return "redirect:/admin/matches/ModifyMatch?error=notfound";
        }

        matchService.deleteById(id);
        return "redirect:/admin/matches/modify-match";
    }

}