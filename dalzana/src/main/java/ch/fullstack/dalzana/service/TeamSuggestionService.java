package ch.fullstack.dalzana.service;

import ch.fullstack.dalzana.model.AppUser;
import ch.fullstack.dalzana.model.Request;
import ch.fullstack.dalzana.model.Skill;
import ch.fullstack.dalzana.repo.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamSuggestionService {

    private final AppUserRepository userRepo;

    public TeamSuggestionService(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<AppUser> suggestTeam(Request request, int teamSize) {
        Set<String> needed = request.getRequiredSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        return userRepo.findAll().stream()
                .filter(u -> u.getRole() != null) // (bei dir role enum)
                .sorted((a, b) -> Integer.compare(score(b, needed), score(a, needed)))
                .limit(teamSize)
                .toList();
    }

    private int score(AppUser user, Set<String> needed) {
        int matches = 0;
        for (Skill s : user.getSkills()) {
            if (needed.contains(s.getName())) matches++;
        }
        return matches;
    }
}
