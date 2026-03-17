package es.footleague.app.repository;

import es.footleague.app.model.Rating;
import es.footleague.app.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
        List<Rating> findByAuthor(User author);
}
