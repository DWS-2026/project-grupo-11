package es.footleague.app.dto;

public record UserUpdateDTO(
    String email,
    Long favouriteTeamId
) {}
