package es.footleague.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return teamRepository.existsById(id);
    }

    public List<Team> findAllOrderByPoints() {
        return teamRepository.findAllByOrderByPointsDesc();
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
        if (teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
        }
    }
}