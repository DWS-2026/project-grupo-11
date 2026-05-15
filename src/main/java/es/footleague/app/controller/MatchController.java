package es.footleague.app.controller;

import es.footleague.app.model.Match;
import es.footleague.app.model.User;
import es.footleague.app.services.MatchService;
import es.footleague.app.services.TeamService;
import es.footleague.app.services.UserService;
import es.footleague.app.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Map;
import java.security.Principal;
import java.util.HashMap;
import java.io.IOException;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

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

    @GetMapping("/modify-match")
    public String adminListMatches(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "ModifyMatch";
    }

    @GetMapping("/match-create")
    public String showCreateForm(Model model) {
        model.addAttribute("match", new Match());
        model.addAttribute("teams", teamService.findAll());
        return "CreateMatch";
    }

    @GetMapping("/match-edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Match> matchOpt = matchService.findById(id);
        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            model.addAttribute("match", match);
            model.addAttribute("teams", teamService.findAll());
            // Weather attribute for the HTML, e.g. climaSoleado, climaLluvioso, etc.
            // depending
            // on match.getWeather()
            // HTML
            model.addAttribute("clima" + match.getWeather(), true);
            model.addAttribute("events", match.getEvents());
            return "EditMatchDetails";
        }
        return "redirect:/admin/matches/modify-match";
    }

    @PostMapping("/match/save")
    @ResponseBody
    public ResponseEntity<?> saveMatch(@ModelAttribute Match match) {
        try {
            // Prepare the match (set stadium, link events, etc.)
            matchService.prepareMatchForSave(match);

            // Save the match (which will also save the events due to cascade settings)
            matchService.save(match);

            // Return a success response with a message
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Partido guardado correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error saving match", e); // Log con contexto
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error",
                            "message", "An unexpected error occurred")); // Genérico
        }
    }

    @PostMapping("/match-delete/{id}")
    public String deleteMatch(@PathVariable Long id) {
        if (matchService.findById(id).isEmpty()) {
            return "redirect:/admin/matches/ModifyMatch?error=notfound";
        }

        matchService.deleteById(id);
        return "redirect:/admin/matches/modify-match";
    }

    /**
     * Upload a match report file from the web interface (browser).
     * 
     * Security: Only ADMIN users can upload reports.
     * The MatchService delegates to FileStorageService which validates paths.
     */
    @PostMapping("/{id}/report/upload")
    public String uploadMatchReportWeb(@PathVariable Long id,
            @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            // Validate file is not empty
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No se ha seleccionado ningún archivo");
                return "redirect:/admin/matches/modify-match?error=emptyfile";
            }

            // Delegate to service (same logic as REST API)
            matchService.uploadReport(id, file);

            redirectAttributes.addFlashAttribute("mensaje", "¡Informe del partido subido correctamente!");
            return "redirect:/admin/matches/modify-match?success=reportuploaded";

        } catch (RuntimeException e) {
            log.error("Match not found: " + id, e);
            redirectAttributes.addFlashAttribute("error", "Partido no encontrado");
            return "redirect:/admin/matches/modify-match?error=notfound";
        } catch (IOException e) {
            log.error("Error uploading report for match ID: " + id, e);
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar el archivo: " + e.getMessage());
            return "redirect:/admin/matches/modify-match?error=uploadfailed";
        }
    }

    /**
     * Download a match report file to the user's device.
     * 
     * Security: Only ADMIN users can download match reports.
     * The FileStorageService validates paths to prevent traversal attacks.
     * 
     */
    @GetMapping("/{id}/report/download")
    public ResponseEntity<?> downloadMatchReport(@PathVariable Long id) {
        try {
            // Delegate to service (same logic as REST API)
            MatchService.ReportResource reportResource = matchService.getMatchReport(id);

            // Determine original filename for download
            String originalFilename = reportResource.fileName() != null
                    ? reportResource.fileName()
                    : "report_match_" + id + ".pdf";

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(reportResource.contentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + originalFilename + "\"")
                    .body(reportResource.resource());

        } catch (RuntimeException e) {
            log.error("Match not found or has no report: " + id, e);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Error downloading match report for ID: " + id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * View a match report file in the browser (if it's an image or PDF).
     * 
     * Security: Only ADMIN users can view reports.
     * The FileStorageService validates paths to prevent traversal attacks.
     */
    @GetMapping("/{id}/report/view")
    public ResponseEntity<?> viewMatchReport(@PathVariable Long id) {
        try {
            // Delegate to service (same logic as REST API)
            MatchService.ReportResource reportResource = matchService.getMatchReport(id);

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(reportResource.contentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + reportResource.fileName() + "\"")
                    .body(reportResource.resource());

        } catch (RuntimeException e) {
            log.error("Match not found or has no report: " + id, e);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Error viewing match report for ID: " + id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

}