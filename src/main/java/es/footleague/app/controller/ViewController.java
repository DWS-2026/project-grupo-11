package es.footleague.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.footleague.app.model.Team;
import es.footleague.app.repository.TeamRepository;

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

}

