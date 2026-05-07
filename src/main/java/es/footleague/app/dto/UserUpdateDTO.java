package es.footleague.app.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateDTO(
    @Email(message = "Email should be valid")
    String email,
    Long favouriteTeamId
) {}
