package es.footleague.app.services;

import es.footleague.app.model.Rating;
import es.footleague.app.model.User;
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

    public List<Rating> findbyUser(User user){
        return ratingRepository.findByAuthor(user);
    }

    public Rating findById(Long userId){
        return ratingRepository.findById(userId).orElse(null);
    }

    public void save(Rating rating) {
        ratingRepository.save(rating);
    }

    public void deleteRating(Long ratingId) {
        ratingRepository.deleteById(ratingId);
    }
    
}
