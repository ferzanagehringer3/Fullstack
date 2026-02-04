package ch.fullstack.dalzana.controller;


import ch.fullstack.dalzana.model.Request;
import ch.fullstack.dalzana.model.RequestStatus;
import ch.fullstack.dalzana.model.Skill;
import ch.fullstack.dalzana.model.Team;
import ch.fullstack.dalzana.repo.TeamRepository;
import ch.fullstack.dalzana.service.RequestService;
import ch.fullstack.dalzana.service.TeamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final TeamService teamService;
    private final TeamRepository teamRepository;

    public RequestController(RequestService requestService, TeamService teamService, TeamRepository teamRepository) {
        this.requestService = requestService;
        this.teamService = teamService;
        this.teamRepository = teamRepository;
    }

    // Liste
    @GetMapping
    public String list(Model model, HttpSession session) {
        var requests = requestService.findAll();
        
        // Erstelle eine Map mit Teams für jeden Request
        Map<Long, List<?>> requestTeamsMap = new HashMap<>();
        for (var request : requests) {
            requestTeamsMap.put(request.getId(), teamRepository.findByRequestId(request.getId()));
        }
        
        model.addAttribute("requests", requests);
        model.addAttribute("requestTeamsMap", requestTeamsMap);
        model.addAttribute("skills", Skill.values());
        model.addAttribute("currentUserRole", session.getAttribute("userRole"));
        return "requests";
    }

    @GetMapping("/cockpit")
    public String cockpit(Model model, HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        Long userId = (Long) session.getAttribute("userId");

        List<Request> requests;
        
        // Manager sehen alle Requests
        if ("MANAGER".equals(userRole)) {
            requests = requestService.findAll();
        } else {
            // Normale User sehen nur Requests, wo sie in einem Team sind
            if (userId == null) {
                requests = List.of();
            } else {
                var userTeams = teamService.findByUserId(userId);
                requests = userTeams.stream()
                        .map(Team::getRequest)
                        .distinct()
                        .toList();
            }
        }

        Map<Long, List<?>> requestTeamsMap = new HashMap<>();
        for (var request : requests) {
            requestTeamsMap.put(request.getId(), teamRepository.findByRequestId(request.getId()));
        }

        model.addAttribute("requests", requests);
        model.addAttribute("requestTeamsMap", requestTeamsMap);
        model.addAttribute("currentUserRole", userRole);
        model.addAttribute("requestStatuses", RequestStatus.values());
        return "cockpit";
    }

    @GetMapping("/create")
    public String createPage(Model model, HttpSession session, RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Requests erstellen.");
            return "redirect:/requests";
        }
        
        model.addAttribute("skills", Skill.values());
        return "request-create";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) List<Skill> requiredSkills,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Nur Manager können Requests erstellen.");
            return "redirect:/requests";
        }
        
        try {
            Request request = new Request(title, description);
            if (requiredSkills != null) {
                requiredSkills.forEach(request::addRequiredSkill);
            }
            requestService.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Request erstellt.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Fehler beim Erstellen: " + e.getMessage());
        }

        return "redirect:/requests";
    }

    // Detail
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        var request = requestService.findById(id);
        var teams = teamRepository.findByRequestId(id);
        model.addAttribute("request", request);
        model.addAttribute("requestTeams", teams);
        model.addAttribute("currentUserRole", session.getAttribute("userRole"));
        return "request-detail";
    }
    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Requests bearbeiten.");
            return "redirect:/requests";
        }
        
        model.addAttribute("request", requestService.findById(id));
        model.addAttribute("skills", Skill.values());
        return "request-edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                      @RequestParam String title,
                      @RequestParam(required = false) String description,
                      @RequestParam(required = false) List<Skill> requiredSkills,
                      @RequestParam RequestStatus status,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Nur Manager können Requests bearbeiten.");
            return "redirect:/requests";
        }
        
        try {
            Request request = requestService.findById(id);
            request.setTitle(title);
            request.setDescription(description);
            request.setStatus(status);
            request.clearRequiredSkills();
            if (requiredSkills != null) {
                requiredSkills.forEach(request::addRequiredSkill);
            }
            requestService.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Request aktualisiert.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Fehler: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Nur Manager können Requests löschen.");
            return "redirect:/requests";
        }
        
        try {
            requestService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Request gelöscht.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Fehler beim Löschen: " + e.getMessage());
        }
        return "redirect:/requests";
    }
    @PostMapping("/{id}/team/suggest")
public String suggestTeam(@PathVariable Long id) {
    // MVP: nimm “irgendeinen Manager” aus DB (später Login)
    Long managerId = 1L;
    var team = teamService.createSuggestedTeam(id, managerId, 3);
    return "redirect:/teams/" + team.getId();
}

    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@PathVariable Long id,
                                               @RequestParam RequestStatus status,
                                               HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            return ResponseEntity.status(403).body("Nur Manager dürfen den Status ändern.");
        }

        try {
            Request request = requestService.findById(id);
            request.setStatus(status);
            requestService.save(request);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler: " + e.getMessage());
        }
    }

}
