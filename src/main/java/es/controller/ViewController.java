package es.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.model.Team;
import es.repository.TeamRepository;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "index"; // si tienes index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "registration";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @Autowired
    private TeamRepository teamRepository; // <--- Tu "herramienta" para sacar datos

    @GetMapping("/classification")
    public String classification(Model model) {
        // Sacamos todos los equipos de la base de datos
        List<Team> listaEquipos = teamRepository.findAll();
        
        // Se los enviamos al HTML
        model.addAttribute("equipos", listaEquipos);
        
        return "classification";
    }
}

