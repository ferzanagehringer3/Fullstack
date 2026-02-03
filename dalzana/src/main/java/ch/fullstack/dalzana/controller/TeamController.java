package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.service.TeamService;
import ch.fullstack.dalzana.repo.AppUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final AppUserRepository userRepository;

    public TeamController(TeamService teamService, AppUserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "team-create";
    }

    @PostMapping("/create")
    public String createTeam(@RequestParam String teamName,
                            @RequestParam(required = false) Long[] userIds,
                            Model model) {
        try {
            Long defaultUserId = 1L;
            if (!userRepository.existsById(defaultUserId)) {
                model.addAttribute("errorMessage", "❌ Kein User gefunden.");
                model.addAttribute("users", userRepository.findAll());
                return "team-create";
            }
            
            var team = teamService.createTeam(teamName, defaultUserId);
            
            if (userIds != null) {
                for (Long userId : userIds) {
                    if (userId != null && !userId.equals(defaultUserId)) {
                        teamService.addMemberToTeam(team.getId(), userId);
                    }
                }
            }
            
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Fehler: " + e.getMessage());
            model.addAttribute("users", userRepository.findAll());
            return "team-create";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.findById(id));
        return "team-detail";
    }
}
