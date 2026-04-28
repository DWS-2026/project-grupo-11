package es.footleague.app.dto;
 
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
 
public record MatchEventDTO(
        Long id,
 
        @NotNull(message = "Minute is required")
        @Min(value = 0, message = "Minute must be between 0 and 120")
        @Max(value = 120, message = "Minute must be between 0 and 120")
        Integer minute,
 
        @NotBlank(message = "Type is required")
        String type,
 
        String namePlayer,
        String namePlayerOut,
        String namePlayerIn,
 
        @NotNull(message = "Match ID is required")
        Long matchId,
 
        @NotNull(message = "Team ID is required")
        Long teamId,
 
        String teamName
) {}