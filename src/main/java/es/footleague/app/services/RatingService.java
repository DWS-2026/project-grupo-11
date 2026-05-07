package es.footleague.app.services;

import es.footleague.app.model.Rating;
import es.footleague.app.model.User;
import es.footleague.app.dto.RatingDTO;
import es.footleague.app.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    public List<Rating> findbyUser(User user) {
        return ratingRepository.findByAuthor(user);
    }

    public Optional<Rating> findById(Long ratingId) {
        return ratingRepository.findById(ratingId);
    }

    public void save(Rating rating) {
        ratingRepository.save(rating);
    }

    public void deleteRating(Long ratingId) {
        ratingRepository.deleteById(ratingId);
    }

    // Find all ratings with pagination
    public Page<Rating> findAll(Pageable pageable) {
        return ratingRepository.findAll(pageable);
    }

    // Update rating only if the user is the owner
    public Optional<Rating> updateRatingIfOwner(Long id, RatingDTO dto, String username) throws AccessDeniedException {
        Optional<Rating> existing = findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        Rating rating = existing.get();

        if (!rating.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only edit your own ratings");
        }

        rating.setScore(dto.score());
        rating.setComment(dto.comment());
        save(rating);

        return Optional.of(rating);
    }

    public Page<Rating> findByAuthorUsername(String username, Pageable pageable) {
        return ratingRepository.findByAuthorUsernameIgnoreCase(username, pageable);
    }

}
