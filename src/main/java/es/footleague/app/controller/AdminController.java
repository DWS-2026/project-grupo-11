package es.footleague.app.controller;

import es.footleague.app.model.User;
import es.footleague.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // --- NAVEGACIÓN GENERAL DEL PANEL ---

    @GetMapping("/Admin_Page")
    public String Admin_Page() {
        return "Admin_Page";
    }

    // --- MENÚS DE GESTIÓN (PANTALLAS PRINCIPALES) ---

    @GetMapping("/accounts-menu")
    public String showAccounts() {
        return "Account_Management_Screen";
    }

    @GetMapping("/teams")
    public String showTeamManagement() {
        return "Team_Management_Screen";
    }

    @GetMapping("/matches")
    public String showMatchManagement() {
        return "Match_Management_Screen";
    }

    // --- GESTIÓN DE USUARIOS / CUENTAS ---

    @GetMapping("/modify-accounts")
    public String modifyAccount(Model model){
        List<User> allUsers = userService.findAll();
        model.addAttribute("users", allUsers);
        return "ModifyAccount";
    } 

    @PostMapping("/users/delete/{username}")
    public String deleteUser(@PathVariable String username){
        Optional<User> userOpt = userService.findByUsernameIgnoreCase(username);
        if (userOpt.isPresent()){
            userService.deleteByUsername(userOpt.get().getUsername());
        }
        return "redirect:/admin/modify-accounts";
    }

    // NOTA: Las rutas de guardar, editar y crear partidos HAN SIDO ELIMINADAS de aquí
    // porque ahora viven en MatchController para evitar errores 400, 404 y 500.
}