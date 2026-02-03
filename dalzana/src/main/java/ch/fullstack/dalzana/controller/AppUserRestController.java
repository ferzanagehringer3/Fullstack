package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.AppUser;
import ch.fullstack.dalzana.model.Skill;
import ch.fullstack.dalzana.model.role;
import ch.fullstack.dalzana.repo.AppUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserRestController {

    private final AppUserRepository userRepo;

    public AppUserRestController(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // 🔹 GET alle Users
    @GetMapping
    public List<AppUser> getAllUsers() {
        return userRepo.findAll();
    }

    // 🔹 GET User by ID
    @GetMapping("/{id}")
    public AppUser getUserById(@PathVariable Long id) {
        return userRepo.findById(id).orElseThrow();
    }

    // ✅ DTO: genau das, was das Frontend schickt
    public static class CreateUserRequest {
        public String name;
        public String email;
        public role role; // erwartet "USER" oder "MANAGER"
        public String password;
        public List<String> skills; // erwartet ["JAVA","PYTHON",...]
    }

    @PostMapping
    public AppUser createUser(@RequestBody CreateUserRequest req) {
        if (req.name == null || req.name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        if (req.email == null || req.email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        if (req.role == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role is required");
        if (req.password == null || req.password.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required");

        // ✅ Constructor nutzen -> profilePicture default wird gesetzt
        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        String passwordHash = encoder.encode(req.password);
        AppUser user = new AppUser(req.name.trim(), req.email.trim(), req.role, passwordHash);

        if (req.skills != null) {
            for (String s : req.skills) {
                if (s == null || s.isBlank())
                    continue;

                try {
                    user.addSkill(Skill.valueOf(s.trim())); // z.B. "JAVA"
                } catch (IllegalArgumentException ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown skill: " + s);
                }
            }
        }

        return userRepo.save(user);
    }
}
