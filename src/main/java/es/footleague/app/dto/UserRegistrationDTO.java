package es.footleague.app.dto;

public record UserRegistrationDTO(
    String username,
    String email,
    String password,
    Long favouriteTeamId
) {}
