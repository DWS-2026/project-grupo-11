package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Controller;; // O @Controller

@Controller // Cambia a RestController si vas a devolver JSON con ResponseEntity
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @PostMapping("/matches") // Es buena práctica poner una ruta
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {
        Match savedMatch = matchRepository.save(match);
        return ResponseEntity.ok(savedMatch);
    }
}