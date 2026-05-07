package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.model.User;
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
import java.sql.Blob;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

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
            }
        }
    }

    @GetMapping("/list-teams")
    public String listTeams(Model model) {
        List<Team> teams = teamService.findAll();
        // Para cada equipo, establecemos la ruta del logo apuntando al endpoint que sirve el Blob
        for (Team team : teams) {
            if (team.getLogoData() != null) {
                team.setLogoFilePath("/api/v1/teams/" + team.getId() + "/logo");
            }
        }
        model.addAttribute("teams", teams);
        return "ModifyTeam"; 
    }

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
     * SAVE/UPDATE: Procesa el archivo como BLOB y lo persiste en MySQL
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
                fileStorageService.validateImageFile(file);
                // Convertimos el MultipartFile a Blob para la columna logo_data[cite: 2]
                byte[] bytes = file.getBytes();
                Blob blob = new SerialBlob(bytes);
                
                team.setLogoData(blob);
                team.setLogoFileName(file.getOriginalFilename());
                // Importante: Ponemos a null el path de archivo físico para usar solo el binario
                team.setLogoFilePath(null); 
            } catch (Exception e) {
                throw new IOException("Error al guardar el logo en la base de datos", e);
            }
        }
        teamService.save(team);
        return "redirect:/admin/teams/list-teams";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Team> teamOpt = teamService.findById(id);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            // Aseguramos que el formulario de edición también use la URL del binario[cite: 2]
            if (team.getLogoData() != null) {
                team.setLogoFilePath("/api/v1/teams/" + team.getId() + "/logo");
            }
            model.addAttribute("team", team);
            return "EditTeam";
        }
        return "redirect:/admin/teams/list-teams";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes info) {
        if (!teamService.canDelete(id)) {
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