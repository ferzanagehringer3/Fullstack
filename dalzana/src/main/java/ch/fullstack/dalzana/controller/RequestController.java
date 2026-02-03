package ch.fullstack.dalzana.controller;


import ch.fullstack.dalzana.service.RequestService;

import ch.fullstack.dalzana.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "requests";
    }

    // Detail
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("request", requestService.findById(id));
        return "request-detail";
    }

    @PostMapping("/{id}/team/suggest")
public String suggestTeam(@PathVariable Long id) {
    // MVP: nimm “irgendeinen Manager” aus DB (später Login)
    Long managerId = 1L;
    var team = teamService.createSuggestedTeam(id, managerId, 3);
    return "redirect:/teams/" + team.getId();
}

}
