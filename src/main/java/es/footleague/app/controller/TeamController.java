package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.model.User;
import es.footleague.app.repository.TeamRepository;
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

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<User> user = userService.findByUsernameIgnoreCase(principal.getName());
            if (user.isPresent()) {
                model.addAttribute("loggedUser", user.get());
                model.addAttribute("logged", true);
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
                // El token lo añade automáticamente tu CSRFHandlerInterceptor
            }
        }
    }

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
    @GetMapping("/team-management-screen")
    public String showTeamManagementScreen(Model model) {
        return "Team_Management_Screen";
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
        return "redirect:/admin/teams/list-teams";
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
            return "EditTeam";
        }
        return "redirect:/admin/teams/list-teams";
    }

    /**
     * ELIMINAR: Accede a /admin/teams/delete/1
     */
    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes info) {
        if (!teamService.canDelete(id)) {
            // Enviamos un mensaje de error que Mustache podrá leer
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