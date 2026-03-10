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
@RequestMapping("/admin")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService; 

    @GetMapping("/list-matches")
    public String adminListMatches(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "ModifyMatch";
    }

    // CAMBIO: Usamos /match-create para que no choque con /match/{id}
    @GetMapping("/match-create")
    public String showCreateForm(Model model) {
        model.addAttribute("match", new Match());
        model.addAttribute("teams", teamService.findAll()); 
        return "CreateMatch";
    }
    
    @GetMapping("/match-edit/{id}") // CAMBIO: Ruta más clara
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Match> matchOpt = matchService.findById(id);
        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("events", match.getEvents());
            model.addAttribute("teams", teamService.findAll());
            return "CreateMatch"; // Reutilizamos CreateMatch
        }
        return "redirect:/admin/list-matches";
    }

    @PostMapping("/match/save")
    public String saveMatch(@ModelAttribute Match match, RedirectAttributes redirectAttributes) {
        try {
            // Lógica de estadio
            if (match.getLocalTeam() != null && match.getLocalTeam().getId() != null) {
                teamService.findById(match.getLocalTeam().getId()).ifPresent(t -> {
                    match.setStadium(t.getStadiumName());
                });
            }

            // Lógica de eventos (MUY IMPORTANTE para evitar Error 500)
            if (match.getEvents() != null) {
                match.getEvents().forEach(event -> event.setMatch(match));
            }

            matchService.save(match);
            redirectAttributes.addFlashAttribute("mensaje", "¡Partido guardado!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // CAMBIO: Redirige a la lista real que existe en este controller
        return "redirect:/admin/list-matches"; 
    }
}