package ch.fullstack.dalzana.service;

import ch.fullstack.dalzana.model.AppUser;
import ch.fullstack.dalzana.model.Request;
import ch.fullstack.dalzana.model.Skill;
import ch.fullstack.dalzana.repo.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamSuggestionService {

    private final AppUserRepository userRepo;

    public TeamSuggestionService(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<AppUser> suggestTeam(Request request, int teamSize) {
        Set<Skill> needed = request.getRequiredSkills();

        return userRepo.findAll().stream()
                .filter(u -> u.getRole() != null)
                .sorted((a, b) -> Integer.compare(score(b, needed), score(a, needed)))
                .limit(teamSize)
                .toList();
    }

    private int score(AppUser user, Set<Skill> needed) {
        int matches = 0;
        for (Skill skill : user.getSkills()) {
            if (needed.contains(skill)) matches++;
        }
        return matches;
    }
}
