package es.footleague.app.dto;

import org.mapstruct.Mapper;
import es.footleague.app.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        Long favouriteTeamId = user.getFavouriteTeam() != null
                ? user.getFavouriteTeam().getId()
                : null;

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                favouriteTeamId,
                user.getRoles());
    }
}
