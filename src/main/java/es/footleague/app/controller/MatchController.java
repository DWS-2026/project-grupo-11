package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.services.MatchService;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService; 

    // SE HA CAMBIADO /match-list A /matches PARA EVITAR EL CONFLICTO
    @GetMapping("/list-matches")
    public String adminListMatches(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "ModifyMatch";
    }

    @GetMapping("/match/new")
    public String showCreateForm(Model model) {
        model.addAttribute("match", new Match());
        model.addAttribute("teams", teamService.findAll()); 
        return "CreateMatch";
    }
    
    @GetMapping("/match/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Match> matchOpt = matchService.findById(id);
        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("events", match.getEvents());
            model.addAttribute("teams", teamService.findAll());

            if (match.getWeather() != null) {
                model.addAttribute("clima" + match.getWeather(), true);
            }
            
            return "EditMatchDetails";
        }
        return "match_not_found";
    }

    @PostMapping("/match/save")
    public String saveMatch(@ModelAttribute Match match, RedirectAttributes redirectAttributes) {
        try {
            if (match.getLocalTeam() != null && match.getLocalTeam().getId() != null) {
                teamService.findById(match.getLocalTeam().getId()).ifPresent(t -> {
                    match.setStadium(t.getStadiumName());
                });
            }

            if (match.getEvents() != null) {
                match.getEvents().forEach(event -> event.setMatch(match));
            }

            matchService.save(match);
            redirectAttributes.addFlashAttribute("mensaje", "Partido guardado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            e.printStackTrace(); 
        }
        // REDIRIGE A LA NUEVA RUTA
        return "redirect:/matches";
    }

    @PostMapping("/match/{id}/delete")
    public String deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
        return "redirect:/matches";
    }
}