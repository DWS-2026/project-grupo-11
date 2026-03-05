package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // LISTAR: Accede a /teams
    @GetMapping
    public String listTeams(Model model) {
        // Mustache buscará una sección {{#teams}}...{{/teams}}
        model.addAttribute("teams", teamService.findAll());
        return "teams_list"; // Archivo: src/main/resources/templates/teams_list.mustache
    }

    // FORMULARIO NUEVO: Accede a /teams/new
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("team", new Team());
        return "team_form"; // Archivo: team_form.mustache
    }

    // GUARDAR/ACTUALIZAR
    @PostMapping("/save")
    public String saveTeam(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam("stadiumName") String stadiumName,
            @RequestParam(value = "logoFile", required = false) MultipartFile file) throws IOException {

        Team team;
        if (id != null) {
            team = teamService.findById(id).orElse(new Team());
        } else {
            team = new Team();
        }

        team.setName(name);
        team.setStadiumName(stadiumName);

        if (file != null && !file.isEmpty()) {
            team.setLogoData(file.getBytes());
        }

        teamService.save(team);
        return "redirect:/teams";
    }

    // EDITAR: Accede a /teams/edit/1
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Team> teamOpt = teamService.findById(id);
        if (teamOpt.isPresent()) {
            model.addAttribute("team", teamOpt.get());
            model.addAttribute("isEdit", true); // Para cambiar el título en Mustache
            return "team_form";
        }
        return "redirect:/teams";
    }

    // ELIMINAR: Accede a /teams/delete/1
    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteById(id);
        return "redirect:/teams";
    }
}