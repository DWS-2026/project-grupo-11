package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.services.MatchService;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/ModifyMatch")
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
            // Atributos para pre-seleccionar clima en el select si usas condiciones en el HTML
            model.addAttribute("clima" + match.getWeather(), true);
            model.addAttribute("events", match.getEvents());
            return "CreateMatch";
        }
        return "redirect:/admin/ModifyMatch";
    }

    @PostMapping("/match/save")
    @ResponseBody
    public ResponseEntity<?> saveMatch(@ModelAttribute Match match) {
        try {
            // 1. Validar y asignar Estadio basado en el equipo local
            if (match.getLocalTeam() != null && match.getLocalTeam().getId() != null) {
                teamService.findById(match.getLocalTeam().getId()).ifPresent(t -> {
                    match.setStadium(t.getStadiumName());
                });
            }

            // 2. Vincular eventos al partido (evita errores de integridad en MySQL)
            if (match.getEvents() != null) {
                match.getEvents().forEach(event -> {
                    if (event != null) {
                        event.setMatch(match);
                    }
                });
            }

            // 3. Guardar en MySQL
            matchService.save(match);

            // 4. Respuesta de éxito para el fetch
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Partido guardado correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}