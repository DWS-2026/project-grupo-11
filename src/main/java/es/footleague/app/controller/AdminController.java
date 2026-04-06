package es.footleague.app.controller;

import es.footleague.app.model.User;
import es.footleague.app.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<User> user = userService.findByUsernameIgnoreCase(principal.getName());
            if (user.isPresent()) {
                model.addAttribute("loggedUser", user.get());
                model.addAttribute("logged", true);
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
                // The token is added automatically by your CSRFHandlerInterceptor
            }
        }
    }

    // --- GENERAL NAVEGATION OF THE PANEL ---

    @GetMapping("/admin-page")
    public String Admin_Page() {
        return "Admin_Page";
    }

    // --- MANAGEMENT MENU (PRINCIPAL SCREENS) ---

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

    // --- USER AND ACCOUNT MANAGEMENT ---

    @GetMapping("/modify-accounts")
    public String modifyAccount(Model model){
        List<User> allUsers = userService.findAll();
        model.addAttribute("users", allUsers);
        return "ModifyAccount";
    } 

    @PostMapping("/users/delete/{username}")
    public String deleteUser(@PathVariable String username){
        Optional<User> userOpt = userService.findByUsernameIgnoreCase(username);
        if(userOpt.isEmpty()){
            return "redirect:/admin/modify-accounts?error=usernotfound";
        }

        if(userOpt.get().getRoles().contains("ADMIN")){
            return "redirect:/admin/modify-accounts?error=cannotdeleteadmin";
        }

        userService.deleteByUsername(userOpt.get().getUsername());

        return "redirect:/admin/modify-accounts";
    }

    // NOTE: the rest of the user management functionalities (like role modification) have been removed from this controller
    // because now they live in MatchController to avoid errors 400, 404 and 500.
}