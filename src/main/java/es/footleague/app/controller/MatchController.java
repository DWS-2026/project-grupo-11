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
import org.springframework.http.ResponseEntity;
import es.footleague.app.model.Team;

@Controller
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService; // Necesario para cargar equipos en el select

    @GetMapping("/match-list")
    public String listMatches(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "match-list";
    }

    @GetMapping("/match/new")
    public String showCreateForm(Model model) {
        model.addAttribute("match", new Match());
        model.addAttribute("teamList", teamService.findAll()); // <--- ¡Asegúrate de que pone teamList!
        return "CreateMatch";
    }
    
    @GetMapping("/match/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Match> matchOpt = matchService.findById(id);
        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("teams", teamService.findAll());

            // --- LÓGICA AÑADIDA PARA EL CLIMA EN MUSTACHE ---
            // Esto activa el atributo "selected" en el <select> del HTML
            if (match.getWeather() != null) {
                // Si el clima es "Lluvia", añade al modelo "climaLluvia" = true
                model.addAttribute("clima" + match.getWeather(), true);
            }
            
            return "CreateMatch";
        }
        return "match_not_found";
    }

    @PostMapping("/match/save")
    public String saveMatch(@ModelAttribute Match match, RedirectAttributes redirectAttributes) {
        try {
            matchService.save(match);
            redirectAttributes.addFlashAttribute("mensaje", "Partido guardado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/match-list";
    }

    @PostMapping("/match/{id}/delete")
    public String deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
        return "redirect:/match-list";
    }
    @PostMapping("/api/matches")
    @ResponseBody // Esto permite que el método responda JSON y no una página
    public ResponseEntity<?> createMatchApi(@RequestBody Match matchData) {
        try {
            // 1. Buscamos el equipo local para obtener su estadio
            Optional<Team> localOpt = teamService.findById(matchData.getLocalTeam().getId());

            // 2. Asignamos el estadio del local al partido (lo que pediste)
            if (localOpt.isPresent()) {
                Team local = localOpt.get();
                matchData.setStadium(local.getStadiumName());
            }

            // 3. Guardamos el partido
            matchService.save(matchData);
        
            return ResponseEntity.ok().body("{\"status\": \"success\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}