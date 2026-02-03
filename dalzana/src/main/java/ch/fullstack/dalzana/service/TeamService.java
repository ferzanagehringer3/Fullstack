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

public Team createTeam(String name, Long creatorId) {
    AppUser creator = userRepo.findById(creatorId).orElseThrow(() -> new RuntimeException("User not found"));
    Request dummyRequest = requestRepo.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("No request found"));
    Team team = new Team(name, dummyRequest, creator);
    team.addMember(creator);
    return teamRepo.save(team);
}

public void addMemberToTeam(Long teamId, Long userId) {
    Team team = teamRepo.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
    AppUser user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    team.addMember(user);
    teamRepo.save(team);
}

}
