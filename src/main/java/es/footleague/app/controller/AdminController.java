package es.footleague.app.controller;

import es.footleague.app.model.User;
import es.footleague.app.services.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Necesario para pasar datos a Mustache
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Necesario para el ID
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;


    @GetMapping("/Admin_Page")
    public String Admin_Page() {
        return "Admin_Page";
    }

    @GetMapping("/teams")
    public String showTeamManagement() {
        return "Team_Management_Screen";
    }

    @GetMapping("/matches")
    public String showMatchManagement() {
        return "Match_Management_Screen";
    }

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
            User user = userOpt.get();
            userService.deleteByUsername(user.getUsername());
        }

        return "redirect:/admin/modify-accounts";
    }

    @GetMapping("/accounts-menu")
    public String showAccounts(Model model){
        return "Account_Management_Screen";
    }

}