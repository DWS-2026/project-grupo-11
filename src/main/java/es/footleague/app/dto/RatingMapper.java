package es.footleague.app.dto;

import org.springframework.stereotype.Service;

import es.footleague.app.model.MatchEvent;
import es.footleague.app.model.Rating;
import es.footleague.app.model.User;

@Service
public class RatingMapper {

    public RatingDTO toDTO(Rating rating) {
        Long authorId = rating.getAuthor() != null ? rating.getAuthor().getId() : null;
        String authorUsername = rating.getAuthor() != null ? rating.getAuthor().getUsername() : null;
        Long eventId = rating.getEvent() != null ? rating.getEvent().getId() : null;

        return new RatingDTO(
                rating.getId(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt(),
                authorUsername,
                authorId,
                eventId
        );
    }

    public Rating toEntity(RatingDTO dto, User author, MatchEvent event) {
        Rating rating = new Rating();
        rating.setId(dto.id());
        rating.setScore(dto.score());
        rating.setComment(dto.comment());
        rating.setCreatedAt(dto.createdAt());
        rating.setAuthor(author);
        rating.setEvent(event);
        return rating;
    }
}
