package es.footleague.app.dto;
 
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
 
public record RatingDTO(
        Long id,
 
        @NotNull(message = "Score is required")
        @Min(value = 1, message = "Score must be between 1 and 10")
        @Max(value = 10, message = "Score must be between 1 and 10")
        Integer score,
 
        @NotBlank(message = "Comment is required")
        String comment,
 
        LocalDateTime createdAt,
        String authorUsername,
        Long authorId,
 
        @NotNull(message = "Event ID is required")
        Long eventId
) {}
