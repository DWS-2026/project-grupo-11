package es.footleague.app.services;

import es.footleague.app.model.Rating;
import es.footleague.app.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    public void save(Rating rating) {
        ratingRepository.save(rating);
    }

    public void deleteRating(Long ratingId) {
        ratingRepository.deleteById(ratingId);
    }
    
    // Método para obtener las valoraciones de un partido específico
    public List<Rating> findByMatchId(Long matchId) {
        // Podrás personalizar esto más adelante en el repository
        return ratingRepository.findAll().stream()
                .filter(r -> r.getMatch().getId().equals(matchId))
                .toList();
    }
}
