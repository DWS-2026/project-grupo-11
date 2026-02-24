package es.footleague.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.footleague.app.model.User;
import es.footleague.app.services.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 1. PROFILE VIEW (To view user data)
    // We use the username because it is unique, as you defined in the entity
    @GetMapping("/profile/{username}")
    public String userProfile(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);

        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            return "user_not_found";
        }
    }

    // 2. REGISTRATION FORM (View)
    @GetMapping("/register")
    public String registerForm() {
        return "registration";
    }

    // 3. PROCESS RECORD (Logic)
    @PostMapping("/register")
    public String processRegister(User user) {
        user.setRole("USER");
        userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile/{username}/my-ratings")
    public String myRatings(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);
        if (user != null) {
            model.addAttribute("user", user);
            return "my-ratings";
        } else {
            return "user_not_found";
        }
    }

    @GetMapping("/profile/{username}/edit")
    public String editProfileForm(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);
        if (user != null) {
            model.addAttribute("user", user);
            return "edit-profile";
        }
        return "user_not_found";
    }

    @PostMapping("/profile/edit")
    public String processEditProfile(User updatedUser) {
        // We look for the original user so as not to lose data that is not in the form
        // (like the role)
        User existingUser = userService.findByUsername(updatedUser.getUsername());

        if (existingUser != null) {
            // 2. Logic for Email: We only update if a new one has been sent and
            // it is not blank
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            
            existingUser.setFavoriteTeam(updatedUser.getFavoriteTeam());

            // We only change the password if you've entered something new
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(updatedUser.getPassword());
            }

            userService.save(existingUser);
        }

        return "redirect:/profile/" + updatedUser.getUsername();
    }
}
