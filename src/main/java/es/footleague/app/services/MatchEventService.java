package es.footleague.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.footleague.app.model.MatchEvent;
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

}
