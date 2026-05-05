package es.footleague.app.dto;

import org.springframework.stereotype.Service;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.model.Match;
import es.footleague.app.model.Team;

@Service
public class MatchEventMapper {

    public MatchEventDTO toDTO(MatchEvent event) {
        String teamName = event.getTeam() != null ? event.getTeam().getName() : null;
        Long matchId = event.getMatch() != null ? event.getMatch().getId() : null;
        Long teamId = event.getTeam() != null ? event.getTeam().getId() : null;

        return new MatchEventDTO(
                event.getId(),
                event.getMinute(),
                event.getType(),
                event.getNamePlayer(),
                event.getNamePlayerOut(),
                event.getNamePlayerIn(),
                matchId,
                teamId,
                teamName
        );
    }

    public MatchEvent toEntity(MatchEventDTO dto, Match match, Team team) {
        MatchEvent event = new MatchEvent();
        event.setId(dto.id());
        event.setMinute(dto.minute());
        event.setType(dto.type());
        event.setNamePlayer(dto.namePlayer());
        event.setNamePlayerOut(dto.namePlayerOut());
        event.setNamePlayerIn(dto.namePlayerIn());
        event.setMatch(match);
        event.setTeam(team);
        return event;
    }

    public MatchEvent createFromDTO(MatchEventDTO dto, Match match, Team team) {
        MatchEvent event = new MatchEvent();
        event.setMinute(dto.minute());
        event.setType(dto.type());
        event.setNamePlayer(dto.namePlayer());
        event.setNamePlayerOut(dto.namePlayerOut());
        event.setNamePlayerIn(dto.namePlayerIn());
        event.setMatch(match);
        event.setTeam(team);
        return event;
    }
}
