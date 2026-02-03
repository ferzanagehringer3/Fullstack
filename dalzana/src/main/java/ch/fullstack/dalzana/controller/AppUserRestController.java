package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.AppUser;
import ch.fullstack.dalzana.repo.AppUserRepository;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
public AppUser createUser(@RequestBody AppUser user) {
    return userRepo.save(user);
}

}
