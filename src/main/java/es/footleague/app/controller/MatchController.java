package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller // Cambia a RestController si vas a devolver JSON con ResponseEntity
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    // 1. LISTADO DE TODOS LOS PARTIDOS
    @GetMapping("/match-list")
    public String listMatches(Model model) {
        // Buscamos todos los partidos en la base de datos
        model.addAttribute("matches", matchRepository.findAll());
        return "match-list"; // Busca el archivo match-list.html
    }

    // 2. DETALLE DE UN PARTIDO
    @GetMapping("/match/{id}")
    public String matchDetail(@PathVariable Long id, Model model) {
        // Buscamos el partido por su ID
        Match match = matchRepository.findById(id).orElseThrow();
        model.addAttribute("match", match);
        return "match-details"; // Busca el archivo match-details.html
    }
}