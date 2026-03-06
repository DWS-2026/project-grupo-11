package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.services.MatchEventService;
import es.footleague.app.services.MatchService;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;
import es.footleague.app.model.Team;
import java.util.List;

@Controller
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService; 

    @Autowired
    private MatchEventService matchEventService;

    // SE HA CAMBIADO /match-list A /matches PARA EVITAR EL CONFLICTO
    @GetMapping("/matches")
    public String listMatches(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "match-list";
    }

    @GetMapping("/match/{id}")
    public String matchDetail(@PathVariable Long id, Model model){
        Optional<Match> matchOpt = matchService.findById(id);

        if(matchOpt.isPresent()){
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("events", match.getEvents());
            model.addAttribute("teams", teamService.findAll());

            model.addAttribute("newEvent", new MatchEvent());

            return "match-details";
        }
        return "match-not-found";
    }

    @GetMapping("/match/{matchId}/event/new")
    public String showCreateEventForm(@PathVariable Long matchId, Model model) {
        Match match = matchService.findById(matchId).orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        model.addAttribute("match", match);
        model.addAttribute("event", new MatchEvent());
        return "create-event";
    }

    @PostMapping("/match/{matchId}/event/save")
    public String createEvent(@PathVariable Long matchId, @RequestParam String type, @RequestParam int minute, @RequestParam String namePlayer, @RequestParam Long teamId) {
        Match match = matchService.findById(matchId).orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        Team team = teamService.findById(teamId).orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        MatchEvent event = new MatchEvent(type, minute, namePlayer, match, team);
        matchEventService.save(event);
        return "redirect:/match/" + matchId;
    }

    @PostMapping("/event/{eventId}/delete")
    public String deleteEvent(@PathVariable Long eventId){
        MatchEvent matchEvent = matchEventService.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        Long matchId = matchEvent.getMatch().getId();
        matchEventService.deleteById(eventId);
        return "redirect:/match/" + matchId;
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