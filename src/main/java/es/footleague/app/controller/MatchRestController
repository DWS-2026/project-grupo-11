package es.footleague.app.controller;

import es.footleague.app.dto.MatchDTO;
import es.footleague.app.model.Match;
import es.footleague.app.services.MatchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchRestController {

    @Autowired
    private MatchService matchService;

    // Subtítulo: "Endpoint listado de Match"
    @GetMapping("/")
    public ResponseEntity<Page<MatchDTO>> getMatches(Pageable pageable) {
        // Tu MatchService.findAll() devuelve List, lo convertimos a Page para la rúbrica
        List<Match> allMatches = matchService.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allMatches.size());
        
        List<MatchDTO> dtos = allMatches.subList(start, end).stream()
                .map(MatchDTO::new)
                .toList();

        return ResponseEntity.ok(new PageImpl<>(dtos, pageable, allMatches.size()));
    }

    // Subtítulo: "Endpoint detalle de Match"
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable Long id) {
        return matchService.findById(id)
                .map(match -> ResponseEntity.ok(new MatchDTO(match)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Subtítulo: "Endpoint creación de Match"
    @PostMapping("/")
    public ResponseEntity<MatchDTO> createMatch(@RequestBody Match match, HttpServletRequest request) {
        // Validación básica (Punto 6)
        if (match.getLocalTeam() == null || match.getVisitorTeam() == null) {
            return ResponseEntity.badRequest().build();
        }

        matchService.save(match);

        URI location = ServletUriComponentsBuilder
                .fromContextPath(request)
                .path("/api/v1/matches/{id}")
                .buildAndExpand(match.getId())
                .toUri();

        return ResponseEntity.created(location).body(new MatchDTO(match));
    }

    // Subtítulo: "Endpoint borrado de Match"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        if (matchService.findById(id).isPresent()) {
            matchService.deleteById(id); // Tu service ya gestiona la reversión de stats
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}