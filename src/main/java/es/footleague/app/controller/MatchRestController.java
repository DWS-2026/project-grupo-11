package es.footleague.app.controller;

import es.footleague.app.dto.MatchDTO;
import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
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
    @GetMapping
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
    @PostMapping
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
    @PutMapping("/{id}")
    public ResponseEntity<MatchDTO> updateMatch(@PathVariable Long id, @RequestBody Match updatedMatch) {
        return matchService.findById(id).map(existingMatch -> {
            
            // 1. Actualizamos datos informativos
            existingMatch.setMatchDate(updatedMatch.getMatchDate());
            existingMatch.setMatchTime(updatedMatch.getMatchTime());
            existingMatch.setWeather(updatedMatch.getWeather());
            existingMatch.setStadium(updatedMatch.getStadium());
            
            // 2. Sincronización de Equipos (por si cambiaron)
            existingMatch.setLocalTeam(updatedMatch.getLocalTeam());
            existingMatch.setVisitorTeam(updatedMatch.getVisitorTeam());

            // 3. Lógica Maestra: Recalcular marcador basado en eventos
            if (updatedMatch.getEvents() != null) {
                // Limpiamos los eventos actuales para reflejar la nueva lista del cliente
                existingMatch.getEvents().clear();
                
                int goalsLocal = 0;
                int goalsVisitor = 0;

                for (MatchEvent event : updatedMatch.getEvents()) {
                    // Vinculamos el evento al partido actual
                    event.setMatch(existingMatch);
                    existingMatch.getEvents().add(event);

                    // Solo si el tipo de evento es "GOL", incrementamos el contador
                    if ("GOL".equalsIgnoreCase(event.getType()) && event.getTeam() != null) {
                        if (event.getTeam().getId().equals(existingMatch.getLocalTeam().getId())) {
                            goalsLocal++;
                        } else if (event.getTeam().getId().equals(existingMatch.getVisitorTeam().getId())) {
                            goalsVisitor++;
                        }
                    }
                }
                
                // Forzamos el marcador según el conteo de eventos
                existingMatch.setLocalGoals(goalsLocal);
                existingMatch.setVisitorGoals(goalsVisitor);
            }

            // 4. Guardar cambios (el Service actualizará las tablas correspondientes)
            matchService.save(existingMatch);
            
            return ResponseEntity.ok(new MatchDTO(existingMatch));
            
        }).orElse(ResponseEntity.notFound().build());
    }
}