package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.Team;
import ch.fullstack.dalzana.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TeamRestController {

    private final TeamService teamService;

    public TeamRestController(TeamService teamService) {
        this.teamService = teamService;
    }

    // alle Teams (für “Teams links” im UI)
    @GetMapping("/teams")
    public List<Team> getTeams() {
        return teamService.findAll();
    }

    // Teams zu einem Request
    @GetMapping("/requests/{requestId}/teams")
    public List<Team> getTeamsForRequest(@PathVariable Long requestId) {
        return teamService.findByRequestId(requestId);
    }

    // Team-Vorschlag erstellen (Postman & UI Button)
    // POST /api/requests/2/teams/suggest?managerId=1&size=3
    @PostMapping("/requests/{requestId}/teams/suggest")
    public Team suggestTeam(@PathVariable Long requestId,
                            @RequestParam Long managerId,
                            @RequestParam(defaultValue = "3") int size) {
        return teamService.createSuggestedTeam(requestId, managerId, size);
    }
}
