package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.model.User;
import es.footleague.app.repository.TeamRepository;
import es.footleague.app.services.FileStorageService;
import es.footleague.app.services.TeamService;
import es.footleague.app.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/teams")
public class TeamController {

    private final TeamRepository teamRepository;
    @Autowired
    private TeamService teamService;

    TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<User> user = userService.findByUsernameIgnoreCase(principal.getName());
            if (user.isPresent()) {
                model.addAttribute("loggedUser", user.get());
                model.addAttribute("logged", true);
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
                // The token is added automatically by your CSRFHandlerInterceptor
            }
        }
    }

    @GetMapping("/list-teams")
    public String listTeams(Model model) {
        model.addAttribute("teams", teamService.findAll());
        return "ModifyTeam"; // the name of the Mustache template to render the list of teams
    }

    /**
     * NEW FORM: It allows access to /admin/teams/new
     * The file should be: src/main/resources/templates/CreateTeam.mustache
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("team", new Team());
        return "CreateTeam";
    }
    @GetMapping("/team-management-screen")
    public String showTeamManagementScreen(Model model) {
        return "Team_Management_Screen";
    }

    /**
     * SAVE/UPDATE: It processes the form data and persists it in MySQL
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
                if (team.getId() == null) {
                    team = teamService.save(team);
                }

                String relativePath = fileStorageService.storeFile(file, "team-logos/" + team.getId(), file.getOriginalFilename());
                team.setLogoFilePath(relativePath);
                team.setLogoFileName(file.getOriginalFilename());
                team.setLogoData(null);
            } catch (Exception e) {
                // Error handling in logo upload [cite: 74]
                throw new IOException("Error al guardar el archivo del logo", e);
            }
        }
        teamService.save(team); // Persistencia en MySQL
        return "redirect:/admin/teams/list-teams";
    }

    /**
     * EDIT: Access /admin/teams/edit/1 to edit the team with id 1
     * It will show the same form as the creation but pre-filled with the team data. The form will submit to the same /admin/teams/save route, which will handle both creation and update logic based on the presence of the id parameter.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Team> teamOpt = teamService.findById(id);
        if (teamOpt.isPresent()) {
            model.addAttribute("team", teamOpt.get());
            return "EditTeam";
        }
        return "redirect:/admin/teams/list-teams";
    }

    /**
     * Delete: Access /admin/teams/delete/1 to delete the team with id 1. It will check if the team can be deleted (i.e., it has no matches associated) and then delete it from MySQL. If it cannot be deleted, it will redirect back to the list with an error message.
     */
    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes info) {
        if (!teamService.canDelete(id)) {
            // EWe send an error message if the team cannot be deleted due to existing matches
            info.addFlashAttribute("error",
                    "No se puede eliminar: El equipo ya tiene partidos registrados en la liga.");
            return "redirect:/admin/teams/list-teams";
        }
        try {
            teamService.deleteById(id);
            info.addFlashAttribute("mensaje", "Equipo eliminado correctamente.");
        } catch (Exception e) {
            info.addFlashAttribute("error", "No se pudo eliminar el equipo debido a un error interno.");
        }

        return "redirect:/admin/teams/list-teams";
    }
}