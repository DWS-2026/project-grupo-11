package es.footleague.app.controller;

import java.nio.file.Path;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;
import es.footleague.app.dto.MatchDTO;
import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
import org.springframework.core.io.Resource;
import java.io.IOException; // Para solucionar el error de IOException
import org.springframework.http.HttpHeaders; // Para solucionar el error de HttpHeaders
import org.springframework.http.MediaType; // Por si te falla MediaType también
import es.footleague.app.services.FileStorageService;
import es.footleague.app.services.MatchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchRestController {
    @Autowired
    private FileStorageService fileStorageService;

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
    @PreAuthorize("hasRole('ADMIN')")
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

                for (MatchEvent event : updatedMatch.getEvents()) {
                    // Vinculamos el evento al partido actual
                    event.setMatch(existingMatch);
                    existingMatch.getEvents().add(event);
                }
                
                // Delegamos el cálculo de goles al servicio
                int goalsLocal = matchService.calculateGoalsFromEventsPublic(existingMatch, existingMatch.getLocalTeam());
                int goalsVisitor = matchService.calculateGoalsFromEventsPublic(existingMatch, existingMatch.getVisitorTeam());
                
                // Establecemos los goles calculados
                existingMatch.setLocalGoals(goalsLocal);
                existingMatch.setVisitorGoals(goalsVisitor);
            }

            // 4. Guardar cambios (el Service actualizará las tablas correspondientes)
            matchService.save(existingMatch);
            
            return ResponseEntity.ok(new MatchDTO(existingMatch));
            
        }).orElse(ResponseEntity.notFound().build());
    }
    // Subtítulo: "Gestión de ficheros: Guardar acta del partido en disco"
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/report")
    public ResponseEntity<MatchDTO> uploadReport(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // 1. Guardar usando tus métodos exactos: storeFile(archivo, subcarpeta, nombreOriginal)
        String relativePath = fileStorageService.storeFile(file, "matches", file.getOriginalFilename()); 

        // 2. Actualizar entidad
        match.setReportFileName(file.getOriginalFilename());
        match.setReportFilePath(relativePath);
        
        matchService.save(match);

        return ResponseEntity.ok(new MatchDTO(match));
    }

    // Subtítulo: "Gestión de ficheros: Visualización del acta"
    @GetMapping("/{id}/report")
        public ResponseEntity<Resource> getMatchReport(@PathVariable Long id) throws IOException {
        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (match.getReportFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        // Usamos el nombre exacto de tu servicio
        Resource resource = fileStorageService.loadFileAsResource(match.getReportFilePath());
        
        // Intentamos detectar el tipo de archivo (PDF, Imagen...)
        String contentType = Files.probeContentType(Path.of(resource.getURI()));
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + match.getReportFileName() + "\"")
                .body(resource);
    }
}