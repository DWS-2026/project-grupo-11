package es.footleague.app.controller;

import es.footleague.app.dto.MatchEventDTO;
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
import org.springframework.http.ResponseEntity;
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

    // GET /api/v1/?page=0&size=10
    @GetMapping("/events")
    public ResponseEntity<Page<MatchEventDTO>> getAllMatchEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<MatchEventDTO> all = matchEventService.findAll()
                .stream()
                .map(this::toDTO)
                .toList();

        int start = page * size;
        int end = Math.min(start + size, all.size());

        if (start > all.size()) {
            return ResponseEntity.ok(Page.empty());
        }

        Page<MatchEventDTO> result = new PageImpl<>(
                all.subList(start, end),
                PageRequest.of(page, size),
                all.size());

        return ResponseEntity.ok(result);
    }

    // GET /api/v1
    @GetMapping("/events/{id}")
    public ResponseEntity<MatchEventDTO> getMatchEvent(@PathVariable Long id) {
        Optional<MatchEvent> eventOp = matchEventService.findById(id);
        if(eventOp.isPresent()){
            MatchEvent event = eventOp.get();
            return ResponseEntity.ok(toDTO(event));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/v1/
    @GetMapping("/matches/{matchId}/events")
    public ResponseEntity<List<MatchEventDTO>> getEventsByMatch(@PathVariable Long matchId) {
        List<MatchEventDTO> events = matchEventService.findAllByMatchId(matchId)
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(events);
    }

    // POST /api/v1
    @PostMapping("/matches/{matchId}/events")
    public ResponseEntity<MatchEventDTO> createMatchEvent(@PathVariable long matchId, @Valid @RequestBody MatchEventDTO dto) {
        Optional<Match> match = matchService.findById(matchId);
        if (match.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Team> team = teamService.findById(dto.teamId());
        if (team.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MatchEvent event = new MatchEvent();
        event.setMinute(dto.minute());
        event.setType(dto.type());
        event.setNamePlayer(dto.namePlayer());
        event.setNamePlayerOut(dto.namePlayerOut());
        event.setNamePlayerIn(dto.namePlayerIn());
        event.setMatch(match.get());
        event.setTeam(team.get());

        matchEventService.save(event);

        URI location = URI.create("/api/v1/events/" + event.getId());
        return ResponseEntity.created(location).body(toDTO(event));
    }

    // PUT /api/v1
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

        return ResponseEntity.ok(toDTO(event));
    }

    // DELETE /api/v1
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteMatchEvent(@PathVariable Long id) {
        Optional<MatchEvent> existing = matchEventService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        matchEventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mapper ---
    private MatchEventDTO toDTO(MatchEvent event) {
        return new MatchEventDTO(
                event.getId(),
                event.getMinute(),
                event.getType(),
                event.getNamePlayer(),
                event.getNamePlayerOut(),
                event.getNamePlayerIn(),
                event.getMatch() != null ? event.getMatch().getId() : null,
                event.getTeam() != null ? event.getTeam().getId() : null,
                event.getTeam() != null ? event.getTeam().getName() : null);
    }
}