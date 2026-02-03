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

public List<Team> findByUserId(Long userId) {
    return teamRepo.findTeamsByMemberId(userId);
}

public List<Team> findByRequestId(Long requestId) {
    return teamRepo.findByRequestId(requestId);
}

public Team createTeam(String name, Long creatorId, Long requestId, String requestDescription) {
    AppUser creator = userRepo.findById(creatorId).orElseThrow(() -> new RuntimeException("User not found"));
    Request request = requestRepo.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));

    if (requestDescription != null && !requestDescription.trim().isEmpty()) {
        request.setDescription(requestDescription.trim());
        requestRepo.save(request);
    }

    Team team = new Team(name, request, creator);
    team.addMember(creator);
    return teamRepo.save(team);
}

public void addMemberToTeam(Long teamId, Long userId) {
    Team team = teamRepo.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
    AppUser user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    team.addMember(user);
    teamRepo.save(team);
}

public void deleteTeam(Long teamId) {
    Team team = teamRepo.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
    teamRepo.delete(team);
}

public void updateTeamName(Long teamId, String name) {
    Team team = teamRepo.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
    if (name != null && !name.trim().isEmpty()) {
        team.setName(name.trim());
        teamRepo.save(team);
    }
}

}
