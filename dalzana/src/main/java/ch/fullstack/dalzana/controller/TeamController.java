package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.RequestStatus;
import ch.fullstack.dalzana.service.TeamService;
import ch.fullstack.dalzana.repo.AppUserRepository;
import ch.fullstack.dalzana.repo.RequestRepository;
import ch.fullstack.dalzana.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final AppUserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestService requestService;

    public TeamController(TeamService teamService, AppUserRepository userRepository, RequestRepository requestRepository, RequestService requestService) {
        this.teamService = teamService;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.requestService = requestService;
    }

    @GetMapping("/create")
    public String createPage(@RequestParam(required = false) Long requestId, Model model, HttpSession session, RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams erstellen.");
            return "redirect:/home";
        }
        
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("requests", requestRepository.findAll());
        model.addAttribute("selectedRequestId", requestId);
        
        if (requestId != null) {
            requestRepository.findById(requestId).ifPresent(request -> {
                model.addAttribute("prefilledDescription", request.getDescription());
            });
        }
        
        return "team-create";
    }

    @PostMapping("/create")
    public String createTeam(@RequestParam String teamName,
                            @RequestParam Long requestId,
                            @RequestParam(required = false) String requestDescription,
                            @RequestParam(required = false) Long[] userIds,
                            Model model,
                            HttpSession session,
                            RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams erstellen.");
            return "redirect:/home";
        }
        
        try {
            Long defaultUserId = 1L;
            if (!userRepository.existsById(defaultUserId)) {
                model.addAttribute("errorMessage", "❌ Kein User gefunden.");
                model.addAttribute("users", userRepository.findAll());
                model.addAttribute("requests", requestRepository.findAll());
                return "team-create";
            }
            
            var team = teamService.createTeam(teamName, defaultUserId, requestId, requestDescription);
            
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
            model.addAttribute("requests", requestRepository.findAll());
            return "team-create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams bearbeiten.");
            return "redirect:/home";
        }
        
        var team = teamService.findById(id);
        Set<Long> memberIds = team.getMembers().stream()
                .map(m -> m.getId())
                .collect(Collectors.toSet());

        model.addAttribute("team", team);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("memberIds", memberIds);
        return "team-edit";
    }

    @PostMapping("/{id}/edit")
    public String editTeam(@PathVariable Long id,
                           @RequestParam(required = false) String teamName,
                           @RequestParam(required = false) Long[] userIds,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams bearbeiten.");
            return "redirect:/home";
        }
        
        try {
            teamService.updateTeamName(id, teamName);
            if (userIds != null) {
                for (Long userId : userIds) {
                    if (userId != null) {
                        teamService.addMemberToTeam(id, userId);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "✅ Team aktualisiert.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Fehler beim Speichern: " + e.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/{id}/delete")
    public String deleteTeam(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams löschen.");
            return "redirect:/home";
        }
        
        try {
            teamService.deleteTeam(id);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Team gelöscht.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Löschen fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        model.addAttribute("team", teamService.findById(id));
        model.addAttribute("currentUserRole", session.getAttribute("userRole"));
        return "team-detail";
    }

    @PostMapping("/{teamId}/request/status")
    public String updateRequestStatus(@PathVariable Long teamId,
                                      @RequestParam RequestStatus status,
                                      RedirectAttributes redirectAttributes) {
        try {
            var team = teamService.findById(teamId);
            if (team.getRequest() != null) {
                var request = team.getRequest();
                request.setStatus(status);
                requestService.save(request);
                redirectAttributes.addFlashAttribute("successMessage", "✅ Status aktualisiert.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Kein Request zugeordnet.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Fehler beim Aktualisieren: " + e.getMessage());
        }
        return "redirect:/teams/" + teamId;
    }
}
