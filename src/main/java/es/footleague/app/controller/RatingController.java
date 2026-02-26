package es.footleague.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import es.footleague.app.model.Rating;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.services.MatchEventService;
import es.footleague.app.services.RatingService;
import es.footleague.app.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired 
    private MatchEventService matchEventService;

    @Autowired
    private UserService userService;

    @GetMapping("/my-ratings")
    public String listRatings(Model model){
        model.addAttribute("ratings", ratingService.findAll());
        return "my-ratings";
    }

    @GetMapping("/match/{matchId}/rating/new")
    public String createRating(@PathVariable Long matchId, Model model){
        model.addAttribute("rating", new Rating());
        model.addAttribute("events", matchEventService.findAllByMatchId(matchId));
        return "player-ratings";
    }

    @PostMapping("/rating/save")
    public String saveRating(@RequestParam Long eventId, @RequestParam int score, @RequestParam String comment, RedirectAttributes info) {
        // 1. Buscamos el evento al que pertenece la valoración
        MatchEvent event = matchEventService.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        // 2. Creamos el objeto Rating y le asignamos todo
        Rating newRating = new Rating();
            newRating.setScore(score);
            newRating.setComment(comment);
            newRating.setEvent(event);
    
        // Si tu Rating también tiene relación directa con Match, la sacamos del evento
        newRating.setMatch(event.getMatch());

        // 3. Guardamos a través del Service
        ratingService.save(newRating);

        // 4. Mensaje de feedback y redirección
        info.addFlashAttribute("mensaje", "⭐ ¡Tu valoración se ha guardado correctamente!");
    
        return "redirect:/my-ratings";
    }
    

    @PostMapping("/rating/{id}/delete")
    public String deleteRating(@PathVariable Long id, RedirectAttributes info){
        ratingService.deleteRating(id);
        info.addFlashAttribute("mensaje", "Valoracion eliminada correctamente");
        return "redirect:/my-ratings";
    }
    
}
