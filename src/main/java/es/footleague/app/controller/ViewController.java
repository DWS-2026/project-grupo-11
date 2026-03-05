package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.services.RatingService;
import es.footleague.app.services.TeamService; // Asegúrate de importar esto
import es.footleague.app.services.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Necesario para pasar datos a Mustache
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Necesario para el ID

@Controller
public class ViewController {

    @Autowired
    private TeamService teamService; // Inyectamos TeamService para buscar el equipo


    @GetMapping("/Admin_Page")
    public String Admin_Page() {
        return "Admin_Page";
    }

    @GetMapping("/CreateMatch")
    public String CreateMatch() {
        return "CreateMatch";
    }

    @GetMapping("/CreateTeam")
    public String CreateTeam() {
        return "CreateTeam";
    }

    @GetMapping("/Team_Management_Screen")
    public String showTeamManagement() {
        return "Team_Management_Screen";
    }

    @GetMapping("/Match_Management_Screen")
    public String showMatchManagement() {
        return "Match_Management_Screen";
    }

    @GetMapping("/ModifyTeam")
    public String listado() {
        return "ModifyTeam";
    }

    @GetMapping("/EditTeam/{id}")
    public String formulario(@PathVariable Long id, Model model) {
        Optional<Team> teamOpt = teamService.findById(id);
        
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            model.addAttribute("team", team);
            return "EditTeam"; 
        }
        
        // Si el equipo no existe, redirigimos al listado
        return "redirect:/ModifyTeam";
    }
}