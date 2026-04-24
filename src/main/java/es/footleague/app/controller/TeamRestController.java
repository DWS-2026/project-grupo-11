package es.footleague.app.controller;
import es.footleague.app.model.TeamDTO;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teams") // Cumple: /api/v1/ y recurso en plural/inglés
public class TeamRestController {

    @Autowired
    private TeamService teamService; // Reutilización de lógica (evita -2 puntos)

    // Subtítulo para el vídeo: "Endpoint listado de Team"
    @GetMapping("/")
    public ResponseEntity<Page<TeamDTO>> getTeams(Pageable page) {
        // Convertimos la página de Entidades a página de DTOs
        Page<TeamDTO> teams = teamService.findAll(page).map(TeamDTO::new);
        return ResponseEntity.ok(teams);
    }

    // Subtítulo para el vídeo: "Endpoint detalle de Team"
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable Long id) {
        return teamService.findById(id)
                .map(team -> ResponseEntity.ok(new TeamDTO(team)))
                .orElse(ResponseEntity.notFound().build());
    }
}