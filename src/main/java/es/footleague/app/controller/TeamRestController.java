package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
public class TeamRestController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<Team> getAllTeams() {
        return teamService.findAll();
    }

    /**
     * Obtener un equipo por ID (Necesario para cargar el formulario de edición)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        Optional<Team> teamOpt = teamService.findById(id);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            return ResponseEntity.ok(team);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Team> createTeam(
            @RequestParam("name") String name,
            @RequestParam("stadiumName") String stadiumName,
            @RequestParam("logoFile") MultipartFile file) throws IOException {

        Team team = new Team(name, stadiumName);

        if (file != null && !file.isEmpty()) {
            team.setLogoData(file.getBytes());
        }

        return ResponseEntity.ok(teamService.save(team));
    }

    /**
     * ACTUALIZAR equipo existente (PUT)
     */
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<Team> updateTeam(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("stadiumName") String stadiumName,
            @RequestParam(value = "logoFile", required = false) MultipartFile file) throws IOException {

        // 1. Buscar el equipo que ya existe en la DB
        Optional<Team> existingTeamOpt = teamService.findById(id);
        if (existingTeamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 2. Modificar sus atributos
        Team existingTeam = existingTeamOpt.get();
        existingTeam.setName(name);
        existingTeam.setStadiumName(stadiumName);

        // 3. Solo actualizar el logo si el usuario subió uno nuevo
        if (file != null && !file.isEmpty()) {
            existingTeam.setLogoData(file.getBytes());
        }

        // 4. Guardar los cambios (save en Spring actualiza si el ID ya existe)
        return ResponseEntity.ok(teamService.save(existingTeam));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}