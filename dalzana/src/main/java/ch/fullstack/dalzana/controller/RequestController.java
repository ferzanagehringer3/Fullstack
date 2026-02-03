package ch.fullstack.dalzana.controller;


import ch.fullstack.dalzana.model.Request;
import ch.fullstack.dalzana.model.Skill;
import ch.fullstack.dalzana.service.RequestService;
import ch.fullstack.dalzana.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final TeamService teamService;

    public RequestController(RequestService requestService, TeamService teamService) {
        this.requestService = requestService;
        this.teamService = teamService;
    }

    // Liste
    @GetMapping
    public String list(Model model) {
        model.addAttribute("requests", requestService.findAll());
        model.addAttribute("skills", Skill.values());
        return "requests";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) List<Skill> requiredSkills,
                         RedirectAttributes redirectAttributes) {
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
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("request", requestService.findById(id));
        return "request-detail";
    }
    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("request", requestService.findById(id));
        model.addAttribute("skills", Skill.values());
        return "request-edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                      @RequestParam String title,
                      @RequestParam(required = false) String description,
                      @RequestParam(required = false) List<Skill> requiredSkills,
                      RedirectAttributes redirectAttributes) {
        try {
            Request request = requestService.findById(id);
            request.setTitle(title);
            request.setDescription(description);
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
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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

}
