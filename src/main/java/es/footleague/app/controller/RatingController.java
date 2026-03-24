package es.footleague.app.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import es.footleague.app.model.Rating;
import es.footleague.app.model.User;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.services.MatchEventService;
import es.footleague.app.services.RatingService;
import es.footleague.app.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RatingController {

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private MatchEventService matchEventService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<User> user = userService.findByUsernameIgnoreCase(principal.getName());
            if (user.isPresent()) {
                model.addAttribute("loggedUser", user.get());
                model.addAttribute("logged", true);
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
                // El token lo añade automáticamente tu CSRFHandlerInterceptor
            }
        }
    }

    @GetMapping("/match/{matchId}/rating/new")
    public String createRating(@PathVariable Long matchId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("rating", new Rating());
        model.addAttribute("events", matchEventService.findAllByMatchId(matchId));
        model.addAttribute("matchId", matchId);
        return "player-ratings";
    }

    @PostMapping("/rating/save")
    public String saveRating(@RequestParam Long eventId, @RequestParam int score, @RequestParam String comment,
            RedirectAttributes info, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        MatchEvent event = matchEventService.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        Rating newRating = new Rating();
        newRating.setScore(score);
        newRating.setComment(comment);
        newRating.setEvent(event);

        Optional<User> userOpt = userService.findByUsernameIgnoreCase(principal.getName());
        if(userOpt.isPresent()){
            newRating.setAuthor(userOpt.get());
        }

        ratingService.save(newRating);

        info.addFlashAttribute("mensaje", "⭐ ¡Tu valoración se ha guardado correctamente!");

        return "redirect:/profile/" + principal.getName() + "/my-ratings";
    }

    @PostMapping("/rating/{id}/delete")
    public String deleteRating(@PathVariable Long id, RedirectAttributes info, Principal principal) {
        Rating rating = ratingService.findById(id);

        if (!rating.getAuthor().getUsername().equalsIgnoreCase(principal.getName())) {
            return "redirect:/403";
        }

        ratingService.deleteRating(id);
        info.addFlashAttribute("mensaje", "Valoracion eliminada correctamente");

        return "redirect:/profile/" + principal.getName() + "/my-ratings";
    }
}
