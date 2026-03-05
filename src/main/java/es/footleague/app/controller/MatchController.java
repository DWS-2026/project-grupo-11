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
import java.util.List;

@Controller
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService; 

    @GetMapping("/match-list")
    public String listMatches(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "match-list";
    }

    @GetMapping("/match/new")
    public String showCreateForm(Model model) {
        model.addAttribute("match", new Match());
        // CAMBIO: Usamos "teams" para ser consistentes con el resto de la app
        model.addAttribute("teams", teamService.findAll()); 
        return "CreateMatch";
    }
    
    @GetMapping("/match/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Match> matchOpt = matchService.findById(id);
        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("teams", teamService.findAll());

            if (match.getWeather() != null) {
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

    /**
     * API para el envío desde JavaScript (match_logic.js)
     * Se mantiene porque tu JS sigue enviando un JSON complejo con eventos.
     */
    @PostMapping("/api/matches")
    @ResponseBody 
    public ResponseEntity<?> createMatchApi(@RequestBody Match matchData) {
        try {
            // 1. Buscamos el equipo local para obtener su estadio
            Optional<Team> localOpt = teamService.findById(matchData.getLocalTeam().getId());
            Optional<Team> visitorOpt = teamService.findById(matchData.getVisitorTeam().getId());

            // 2. Asignamos el estadio y aseguramos que los objetos Team estén completos
            if (localOpt.isPresent() && visitorOpt.isPresent()) {
                matchData.setLocalTeam(localOpt.get());
                matchData.setVisitorTeam(visitorOpt.get());
                matchData.setStadium(localOpt.get().getStadiumName());
            }

            // 3. Guardamos el partido
            matchService.save(matchData);
        
            return ResponseEntity.ok().body("{\"status\": \"success\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}