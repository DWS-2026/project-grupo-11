package es.footleague.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view-errors")
public class ErrorTestController {

    @GetMapping("/400")
    public String view400() { return "error/400"; } // Pase Impreciso

    @GetMapping("/403")
    public String view403() { return "error/403"; } // Zona Exclusiva

    @GetMapping("/404")
    public String view404() { return "error/404"; } // Fuera de Juego

    @GetMapping("/409")
    public String view409() { return "error/409"; } // Jugada Repetida

    @GetMapping("/500")
    public String view500() { return "error/500"; } // Tarjeta Roja

    @GetMapping("/503")
    public String view503() { return "error/503"; } // En el Banquillo
}