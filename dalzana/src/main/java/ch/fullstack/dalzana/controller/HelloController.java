package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.service.TeamService;
import ch.fullstack.dalzana.repo.AppUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {

    private final TeamService teamService;
    private final AppUserRepository userRepository;

    public HelloController(TeamService teamService, AppUserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
public String root() {
    return "redirect:/login";
}

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "home";
    }

    @PostMapping("/home/team/create")
    public String createTeam(@RequestParam String teamName, Model model) {
        try {
            Long defaultUserId = 1L;
            if (!userRepository.existsById(defaultUserId)) {
                model.addAttribute("errorMessage", "❌ Kein User gefunden. Bitte registrieren Sie sich zuerst.");
            } else {
                teamService.createTeam(teamName, defaultUserId);
                model.addAttribute("successMessage", "✅ Team erstellt!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Fehler beim Erstellen des Teams: " + e.getMessage());
        }
        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "home";
    }

    @PostMapping("/home/team/addMember")
    public String addMemberToTeam(@RequestParam Long teamId, @RequestParam Long userId, Model model) {
        try {
            teamService.addMemberToTeam(teamId, userId);
            model.addAttribute("successMessage", "✅ Member hinzugefügt!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Fehler beim Hinzufügen: " + e.getMessage());
        }
        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "home";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
