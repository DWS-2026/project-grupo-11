package es.footleague.app.controller;

import java.nio.file.Path;
import java.nio.file.Files;
import es.footleague.app.dto.MatchDTO;
import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
import org.springframework.core.io.Resource;
import java.io.IOException; 
import org.springframework.http.HttpHeaders; 
import org.springframework.http.MediaType; 
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchRestController {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MatchService matchService;

    // Subtitle: "Match listing endpoint"
    @GetMapping
    public ResponseEntity<Page<MatchDTO>> getMatches(Pageable pageable) {
        // MatchService.findAll() returns List, we convert it to Page for the rubric
        List<Match> allMatches = matchService.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allMatches.size());
        
        List<MatchDTO> dtos = allMatches.subList(start, end).stream()
                .map(MatchDTO::new)
                .toList();

        return ResponseEntity.ok(new PageImpl<>(dtos, pageable, allMatches.size()));
    }

    // Subtitle: "Match detail endpoint"
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable Long id) {
        return matchService.findById(id)
                .map(match -> ResponseEntity.ok(new MatchDTO(match)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Subtitle: "Match creation endpoint"
    @PostMapping
    public ResponseEntity<MatchDTO> createMatch(@RequestBody Match match, HttpServletRequest request) {
        // Basic validation (Point 6)
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

    // Subtitle: "Match deletion endpoint"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        if (matchService.findById(id).isPresent()) {
            matchService.deleteById(id); // The service already handles stats rollback
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MatchDTO> updateMatch(@PathVariable Long id, @RequestBody Match updatedMatch) {
        return matchService.findById(id).map(existingMatch -> {
            
            // 1. Update informational data
            existingMatch.setMatchDate(updatedMatch.getMatchDate());
            existingMatch.setMatchTime(updatedMatch.getMatchTime());
            existingMatch.setWeather(updatedMatch.getWeather());
            existingMatch.setStadium(updatedMatch.getStadium());
            
            // 2. Team sync (in case they changed)
            existingMatch.setLocalTeam(updatedMatch.getLocalTeam());
            existingMatch.setVisitorTeam(updatedMatch.getVisitorTeam());

            // 3. Master Logic: Recalculate score based on events
            if (updatedMatch.getEvents() != null) {
                // Clear current events to reflect the new list from the client
                existingMatch.getEvents().clear();

                for (MatchEvent event : updatedMatch.getEvents()) {
                    // Link the event to the current match
                    event.setMatch(existingMatch);
                    existingMatch.getEvents().add(event);
                }
                
                 // Delegate goal calculation to the service
                int goalsLocal = matchService.calculateGoalsFromEventsPublic(existingMatch, existingMatch.getLocalTeam());
                int goalsVisitor = matchService.calculateGoalsFromEventsPublic(existingMatch, existingMatch.getVisitorTeam());
                
                // Set the calculated goals
                existingMatch.setLocalGoals(goalsLocal);
                existingMatch.setVisitorGoals(goalsVisitor);
            }

            // // 4. Save changes (the Service will update the corresponding tables)
            matchService.save(existingMatch);
            
            return ResponseEntity.ok(new MatchDTO(existingMatch));
            
        }).orElse(ResponseEntity.notFound().build());
    }
    // Subtitle: "File management: Save match report to disk"
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/report")
    public ResponseEntity<MatchDTO> uploadReport(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // 1. Save using exact methods: storeFile(file, subfolder, originalName)
        String relativePath = fileStorageService.storeFile(file, "matches", file.getOriginalFilename()); 

        // 2. Update entity
        match.setReportFileName(file.getOriginalFilename());
        match.setReportFilePath(relativePath);
        
        matchService.save(match);

        return ResponseEntity.ok(new MatchDTO(match));
    }

    // Subtitle: "File management: Match report display"
    @GetMapping("/{id}/report")
        public ResponseEntity<Resource> getMatchReport(@PathVariable Long id) throws IOException {
        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (match.getReportFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        // Using the exact method name from the service
        Resource resource = fileStorageService.loadFileAsResource(match.getReportFilePath());
        
        // Try to detect the file type (PDF, Image...)
        String contentType = Files.probeContentType(Path.of(resource.getURI()));
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + match.getReportFileName() + "\"")
                .body(resource);
    }
}