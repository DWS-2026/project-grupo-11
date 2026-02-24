package es.footleague.app.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.footleague.app.model.Team;
import es.footleague.app.repository.TeamRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    /**
     * Obtiene la lista de todos los equipos registrados.
     */
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    /**
     * Busca un equipo por su ID único.
     */
    public Team findById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    /**
     * Guarda un equipo. Sirve tanto para crear uno nuevo 
     * como para actualizar uno existente.
     */
    public Team save(Team team) { // Cambiado de void a Team
        return teamRepository.save(team); // Añadido return
    }

    /**
     * Elimina un equipo de la base de datos por su ID.
     */
    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }
}