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
@RequestMapping("/admin/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/list-teams")
    public String listTeams(Model model) {
        model.addAttribute("teams", teamService.findAll());
        return "ModifyTeam"; // El nombre de tu archivo .mustache o .html
    }

    /**
     * FORMULARIO NUEVO: Accede a /admin/teams/new
     * El archivo debe ser: src/main/resources/templates/CreateTeam.mustache
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("team", new Team());
        return "CreateTeam";
    }

    /**
     * GUARDAR/ACTUALIZAR
     * Procesa los datos y los persiste en MySQL
     */
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
            try {
                team.setLogoData(new javax.sql.rowset.serial.SerialBlob(file.getBytes()));
            } catch (Exception e) {
                // Manejo de error al crear el objeto binario [cite: 74]
                throw new IOException("Error al crear el blob del logo", e);
            }
        }
        teamService.save(team); // Persistencia en MySQL
        return "redirect:/admin/teams";
    }

    /**
     * EDITAR: Accede a /admin/teams/edit/1
     * Reutiliza la plantilla de creación
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Team> teamOpt = teamService.findById(id);
        if (teamOpt.isPresent()) {
            model.addAttribute("team", teamOpt.get());
            return "CreateTeam";
        }
        return "redirect:/admin/teams";
    }

    /**
     * ELIMINAR: Accede a /admin/teams/delete/1
     */
    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteById(id);
        return "redirect:/admin/teams";
    }
}