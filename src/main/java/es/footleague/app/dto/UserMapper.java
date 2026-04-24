package es.footleague.app.dto;

import org.mapstruct.Mapper;
import es.footleague.app.model.User;

@Mapper(componentModel = "spring")
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
