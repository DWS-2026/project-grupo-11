package es.footleague.app.controller;

import es.footleague.app.model.Team;
import es.footleague.app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.footleague.app.services.TeamService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamRestController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<Team> getAllTeams() {
        return teamService.findAll();
    }

    // ELIMINA el método createTeam(@RequestBody Team team) anterior

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Team> createTeam(
        @RequestParam("name") String name,
        @RequestParam("stadiumName") String stadiumName,
        @RequestParam("logoFile") MultipartFile file) throws IOException {

        Team team = new Team(name, stadiumName);
        
        if (file != null && !file.isEmpty()) {
            team.setLogoData(file.getBytes()); // Guardamos los bytes en el campo @Lob
            team.setLogoPath(file.getOriginalFilename());
        }

        return ResponseEntity.ok(teamService.save(team));
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamService.deleteById(id);
    }
}