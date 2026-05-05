package es.footleague.app.controller;

import es.footleague.app.dto.MatchEventDTO;
import es.footleague.app.dto.MatchEventMapper;
import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.model.Team;
import es.footleague.app.services.MatchEventService;
import es.footleague.app.services.MatchService;
import es.footleague.app.services.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class MatchEventRestController {

    @Autowired
    private MatchEventService matchEventService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchEventMapper matchEventMapper;

    // GET /api/v1/events?page=0&size=10
    @GetMapping("/events")
    public ResponseEntity<Page<MatchEventDTO>> getAllMatchEvents(Pageable pageable) {
        Page<MatchEvent> events = matchEventService.findAll(pageable);
        Page<MatchEventDTO> dtos = events.map(matchEventMapper::toDTO);
        return ResponseEntity.ok(dtos);
    }

    // GET /api/v1/events/{id}
    @GetMapping("/events/{id}")
    public ResponseEntity<MatchEventDTO> getMatchEvent(@PathVariable Long id) {
        Optional<MatchEvent> eventOp = matchEventService.findById(id);
        return eventOp.isPresent() 
            ? ResponseEntity.ok(matchEventMapper.toDTO(eventOp.get()))
            : ResponseEntity.notFound().build();
    }

    // GET /api/v1/matches/{matchId}/events
    @GetMapping("/matches/{matchId}/events")
    public ResponseEntity<List<MatchEventDTO>> getEventsByMatch(@PathVariable Long matchId) {
        List<MatchEventDTO> events = matchEventService.findAllByMatchId(matchId)
                .stream()
                .map(matchEventMapper::toDTO)
                .toList();
        return ResponseEntity.ok(events);
    }

    // POST /api/v1/matches/{matchId}/events
    @PostMapping("/matches/{matchId}/events")
    public ResponseEntity<MatchEventDTO> createMatchEvent(@PathVariable long matchId, @Valid @RequestBody MatchEventDTO dto) {
        try {
            Optional<Match> match = matchService.findById(matchId);
            if (match.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Team> team = teamService.findById(dto.teamId());
            if (team.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MatchEvent event = matchEventService.create(dto, matchId, match.get(), team.get());

            URI location = URI.create("/api/v1/events/" + event.getId());
            return ResponseEntity.created(location).body(matchEventMapper.toDTO(event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/v1/events/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/events/{id}")
    public ResponseEntity<MatchEventDTO> updateMatchEvent(
            @PathVariable Long id,
            @Valid @RequestBody MatchEventDTO dto) {

        Optional<MatchEvent> existing = matchEventService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Match> match = matchService.findById(dto.matchId());
        if (match.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Team> team = teamService.findById(dto.teamId());
        if (team.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MatchEvent event = existing.get();
        event.setMinute(dto.minute());
        event.setType(dto.type());
        event.setNamePlayer(dto.namePlayer());
        event.setNamePlayerOut(dto.namePlayerOut());
        event.setNamePlayerIn(dto.namePlayerIn());
        event.setMatch(match.get());
        event.setTeam(team.get());

        matchEventService.save(event);

        return ResponseEntity.ok(matchEventMapper.toDTO(event));
    }

    // DELETE /api/v1/events/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteMatchEvent(@PathVariable Long id) {
        Optional<MatchEvent> existing = matchEventService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        matchEventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Mapper moved to MatchEventMapper.java
}