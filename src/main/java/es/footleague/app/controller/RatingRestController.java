package es.footleague.app.controller;
 
import es.footleague.app.dto.RatingDTO;
import es.footleague.app.dto.RatingMapper;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.model.Rating;
import es.footleague.app.model.User;
import es.footleague.app.services.MatchEventService;
import es.footleague.app.services.RatingService;
import es.footleague.app.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
 
import java.net.URI;
import java.util.List;
import java.util.Optional;
 
@RestController
@RequestMapping("/api/v1")
public class RatingRestController {
 
    @Autowired
    private RatingService ratingService;
 
    @Autowired
    private MatchEventService matchEventService;
 
    @Autowired
    private UserService userService;

    @Autowired
    private RatingMapper ratingMapper;
 
    // GET /api/v1/ratings?page=0&size=10
    @GetMapping("/ratings")
    public ResponseEntity<Page<RatingDTO>> getAllRatings(Pageable pageable) {
        Page<Rating> ratings = ratingService.findAll(pageable);
        Page<RatingDTO> dtos = ratings.map(ratingMapper::toDTO);
        return ResponseEntity.ok(dtos);
    }
 
    // GET /api/v1/ratings/{id}
    @GetMapping("/ratings/{id}")
    public ResponseEntity<RatingDTO> getRatingById(@PathVariable Long id) {
        Optional<Rating> ratingOp = ratingService.findById(id);
        return ratingOp.isPresent() 
            ? ResponseEntity.ok(ratingMapper.toDTO(ratingOp.get()))
            : ResponseEntity.notFound().build();
    }
 
    // POST /api/v1/events/{eventId}/ratings
    @PostMapping("/events/{eventId}/ratings")
    public ResponseEntity<RatingDTO> createRating(
            @Valid @RequestBody RatingDTO dto,
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable long eventId) {
 
        Optional<MatchEvent> event = matchEventService.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
 
        Optional<User> author = userService.findByUsernameIgnoreCase(userDetails.getUsername());
        if (author.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
 
        Rating rating = new Rating(dto.score(), dto.comment(), author.get(), event.get());
        ratingService.save(rating);
 
        URI location = URI.create("/api/v1/ratings/" + rating.getId());
        return ResponseEntity.created(location).body(ratingMapper.toDTO(rating));
    }
 
    // PUT /api/v1/ratings/{id}
    @PutMapping("/ratings/{id}")
    public ResponseEntity<RatingDTO> updateRating(
            @PathVariable Long id,
            @Valid @RequestBody RatingDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Optional<Rating> updated = ratingService.updateRatingIfOwner(id, dto, userDetails.getUsername());
            return updated.isPresent() 
                ? ResponseEntity.ok(ratingMapper.toDTO(updated.get()))
                : ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }
 
    // DELETE /api/v1/ratings/{id}
    @DeleteMapping("/ratings/{id}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
 
        Optional<Rating> existing = ratingService.findById(id);
        if (!existing.isPresent()) {
            return ResponseEntity.notFound().build();
        }
 
        // Only the owner or an admin can delete
        boolean isOwner = existing.get().getAuthor().getUsername().equals(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).build();
        }
 
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}

    // Mapper moved to RatingMapper.java
