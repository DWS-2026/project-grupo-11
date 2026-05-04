package es.footleague.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.footleague.app.model.Match;
import es.footleague.app.model.MatchEvent;
import es.footleague.app.model.Team;
import es.footleague.app.dto.MatchEventDTO;
import es.footleague.app.repository.MatchEventRepository;

@Service
public class MatchEventService {

    @Autowired
    private MatchEventRepository matchEventRepository;

    public List<MatchEvent> findAll(){
        return matchEventRepository.findAll();
    }

    public void save(MatchEvent event){
        if(event.getMinute() < 0 || event.getMinute() > 120){
            throw new IllegalArgumentException("El minuto debe estar entre 0 y 120");
        }
        matchEventRepository.save(event);
    }

    public Optional<MatchEvent> findById(long id){
        return matchEventRepository.findById(id);
    }

    public List<MatchEvent> findAllByMatchId(Long matchId){
        return matchEventRepository.findAll().stream().filter(matchEvent -> matchEvent.getMatch().getId().equals(matchId)).toList();
    }

    public void deleteById(long id) {
        matchEventRepository.deleteById(id);
	}

    // Find all events with pagination
    public Page<MatchEvent> findAll(Pageable pageable) {
        return matchEventRepository.findAll(pageable);
    }

    // Create a new match event from DTO with validation
    public MatchEvent create(MatchEventDTO dto, Long matchId, Match match, Team team) throws Exception {
        if (match == null) {
            throw new Exception("Match not found");
        }

        if (team == null) {
            throw new Exception("Team not found");
        }

        MatchEvent event = new MatchEvent();
        event.setMinute(dto.minute());
        event.setType(dto.type());
        event.setNamePlayer(dto.namePlayer());
        event.setNamePlayerOut(dto.namePlayerOut());
        event.setNamePlayerIn(dto.namePlayerIn());
        event.setMatch(match);
        event.setTeam(team);

        save(event);
        return event;
    }

}
