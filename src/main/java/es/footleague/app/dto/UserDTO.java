package es.footleague.app.dto;

import java.util.List;

public record UserDTO(
        Long id,
        String username,
        String email,
        Long favouriteTeamId,
        List<String> roles) {
}
