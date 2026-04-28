package es.footleague.app.controller;
 
import es.footleague.app.dto.RatingDTO;
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
import org.springframework.http.ResponseEntity;
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
 
    // GET /api/v1?page=0&size=10
    @GetMapping("/ratings")
    public ResponseEntity<Page<RatingDTO>> getAllRatings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
 
        List<RatingDTO> all = ratingService.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
 
        int start = page * size;
        int end = Math.min(start + size, all.size());
 
        if (start > all.size()) {
            return ResponseEntity.ok(Page.empty());
        }
 
        Page<RatingDTO> result = new PageImpl<>(
                all.subList(start, end),
                PageRequest.of(page, size),
                all.size()
        );
 
        return ResponseEntity.ok(result);
    }
 
    // GET /api/v1
    @GetMapping("/ratings/{id}")
    public ResponseEntity<RatingDTO> getRatingById(@PathVariable Long id) {
        Optional<Rating> ratingOp = ratingService.findById(id);
        if(ratingOp.isPresent()){
            Rating rating = ratingOp.get();
            return ResponseEntity.ok(toDTO(rating));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
 
    // POST /api/v1
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
        return ResponseEntity.created(location).body(toDTO(rating));
    }
 
    // PUT /api/v1
    @PutMapping("/ratings/{id}")
    public ResponseEntity<RatingDTO> updateRating(
            @PathVariable Long id,
            @Valid @RequestBody RatingDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
 
        Optional<Rating> existing = ratingService.findById(id);
        if (!existing.isPresent()) {
            return ResponseEntity.notFound().build();
        }
 
        // Only the owner can edit their rating
        if (!existing.get().getAuthor().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }

        Rating rating = existing.get();
        rating.setScore(dto.score());
        rating.setComment(dto.comment());
        ratingService.save(rating);
 
        return ResponseEntity.ok(toDTO(rating));
    }
 
    // DELETE /api/v1
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
 
    // --- Mapper ---
    private RatingDTO toDTO(Rating rating) {
        return new RatingDTO(
                rating.getId(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt(),
                rating.getAuthor() != null ? rating.getAuthor().getUsername() : null,
                rating.getAuthor() != null ? rating.getAuthor().getId() : null,
                rating.getEvent() != null ? rating.getEvent().getId() : null
        );
    }
}
