package ch.fullstack.dalzana.service;

import ch.fullstack.dalzana.model.*;
import ch.fullstack.dalzana.repo.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepo;
    private final RequestRepository requestRepo;
    private final AppUserRepository userRepo;
    private final TeamSuggestionService suggestionService;

    public TeamService(TeamRepository teamRepo, RequestRepository requestRepo,
                       AppUserRepository userRepo, TeamSuggestionService suggestionService) {
        this.teamRepo = teamRepo;
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.suggestionService = suggestionService;
    }

    public Team createSuggestedTeam(Long requestId, Long managerUserId, int teamSize) {
        Request req = requestRepo.findById(requestId).orElseThrow();
        AppUser manager = userRepo.findById(managerUserId).orElseThrow();

        Team team = new Team("Team for: " + req.getTitle(), req, manager);

        for (AppUser u : suggestionService.suggestTeam(req, teamSize)) {
            team.addMember(u);
        }

        return teamRepo.save(team);
    }

    public Team findById(Long id) {
        return teamRepo.findById(id).orElseThrow();
    }

    public List<Team> findAll() {
    return teamRepo.findAll();
}

public List<Team> findByRequestId(Long requestId) {
    return teamRepo.findByRequestId(requestId);
}

}
