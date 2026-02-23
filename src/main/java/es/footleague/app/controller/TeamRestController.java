package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams") // Esta es la ruta que llama tu fetch() en JS
public class TeamRestController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<Team> getAllTeams() {
        // Esto devuelve la lista de equipos (Madrid, Barça...) como JSON
        return teamService.findAll();
    }
}