package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.Skill;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @GetMapping
    public List<SkillDTO> getAllSkills() {
        return Arrays.stream(Skill.values())
                .map(skill -> new SkillDTO(skill.name(), skill.getDisplayName()))
                .collect(Collectors.toList());
    }

    public static class SkillDTO {
        private String name;
        private String displayName;

        public SkillDTO(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }

        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
    }
}
