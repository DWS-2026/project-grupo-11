package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.repository.MatchRepository;
import es.footleague.app.services.MatchService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller 
public class MatchController {

    @Autowired
    private MatchService matchService;

    // 1. LISTADO DE TODOS LOS PARTIDOS
    @GetMapping("/match-list")
    public String listMatches(Model model) {
        // Buscamos todos los partidos en la base de datos
        model.addAttribute("matches", matchService.findAll());
        return "match-list"; // Busca el archivo match-list.html
    }

    // 2. DETALLE DE UN PARTIDO
    @GetMapping("/match/{id}")
    public String matchDetail(@PathVariable Long id, Model model) {
        // Buscamos el partido por su ID
        Optional<Match> match = matchService.findById(id);
        if(match.isPresent()){
            model.addAttribute("match", match);
            return "match-details"; // Busca el archivo match-details.html
        } else {
            return "match_not_found";
        }
    }

    @PostMapping("/match/{id}/delete")
    public String deleteMatch(@PathVariable long id, RedirectAttributes redirectAttributes) {
        // Buscamos el partido por su ID
        Optional<Match> match = matchService.findById(id);
        if(match.isPresent()){
            matchService.deleteById(id);
            // 2. Preparar el mensaje de éxito para el usuario
            redirectAttributes.addFlashAttribute("mensaje", "Partido eliminado correctamente");
            return "redirect:/match";
        } else {
            return "match_not_found";
        }
    }
    
}