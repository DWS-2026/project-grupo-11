package es.footleague.app.controller;

import es.footleague.app.services.RatingService;
import es.footleague.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ViewController {

    // 1. Inyectamos el servicio (Arquitectura en capas)
    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService; // Inyectamos RatingService

    @GetMapping("/")
    public String home() {
        return "index"; // si tienes index.html
    }

    @GetMapping("/login")
    public String showlogin() {
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

    @GetMapping("/classification")
    public String classification() {
        return "classification";
    }

    @GetMapping("/match-details")
    public String matchDetails() {
        return "match-details";
    }

    @GetMapping("/match-list")
    public String matchList(){
        return "match-list";
    }
    @GetMapping("/Admin_Page")
    public String Admin_Page() {
        return "Admin_Page";
    }
    @GetMapping("/CreateMatch")
    public String CreateMatch() {
        return "CreateMatch";
    }
}

