package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.service.TeamService;
import jakarta.servlet.http.HttpSession;
import ch.fullstack.dalzana.model.AppUser;
import ch.fullstack.dalzana.model.Skill;
import ch.fullstack.dalzana.model.role;
import ch.fullstack.dalzana.repo.AppUserRepository;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;

@Controller
public class HelloController {

    private final TeamService teamService;
    private final AppUserRepository userRepository;
    private final Argon2PasswordEncoder encoder;

    public HelloController(TeamService teamService, AppUserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
        this.encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    } 

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {

        var userOpt = userRepository.findByEmailIgnoreCase(email.trim());

        if (userOpt.isEmpty()) {
            ra.addFlashAttribute("loginError", "Unbekannte Email oder falsches Passwort.");
            return "redirect:/login";
        }

        AppUser user = userOpt.get();

        if (!encoder.matches(password, user.getPasswordHash())) {
            ra.addFlashAttribute("loginError", "Unbekannte Email oder falsches Passwort.");
            return "redirect:/login";
        }

        // ✅ Session setzen
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userRole", user.getRole().name());

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/login";

        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentUserName", session.getAttribute("userName"));
        model.addAttribute("currentUserRole", session.getAttribute("userRole"));
        return "home";
    }

    @PostMapping("/home/team/create")
    public String createTeam(@RequestParam String teamName, @RequestParam(required = false) Long userId, @RequestParam(required = false) String description, Model model) {
        try {
            Long defaultUserId = userId != null ? userId : 1L;
            if (!userRepository.existsById(defaultUserId)) {
                model.addAttribute("errorMessage", "❌ Kein User gefunden. Bitte registrieren Sie sich zuerst.");
            } else {
                teamService.createTeam(teamName, defaultUserId, defaultUserId, description != null ? description : "");
                model.addAttribute("successMessage", "✅ Team erstellt!");
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/login";

        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("allSkills", Skill.values());
        return "profile";
    }

    @PostMapping("/profile/update")
        public String updateProfile(@RequestParam String name,
            @RequestParam String email,
            @RequestParam String roleParam,
            @RequestParam(value = "skills", required = false) String[] skills,
            HttpSession session,
            RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/login";

        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return "redirect:/login";

        try {
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setRole(role.valueOf(roleParam.trim()));

            Set<Skill> skillSet = EnumSet.noneOf(Skill.class);
            if (skills != null) {
                for (String s : skills) {
                    skillSet.add(Skill.valueOf(s));
                }
            }
            user.setSkills(skillSet);

            userRepository.save(user);
            ra.addFlashAttribute("successMessage", "Profil aktualisiert.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Update fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/upload")
    public String uploadProfilePicture(@RequestParam("profileImage") MultipartFile profileImage,
            HttpSession session,
            RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/login";

        if (profileImage == null || profileImage.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Bitte ein Bild auswählen.");
            return "redirect:/profile";
        }

        String contentType = profileImage.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            ra.addFlashAttribute("errorMessage", "Nur Bilddateien sind erlaubt.");
            return "redirect:/profile";
        }

        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return "redirect:/login";

        try {
            String base64 = Base64.getEncoder().encodeToString(profileImage.getBytes());
            String dataUrl = "data:" + contentType + ";base64," + base64;
            user.setProfilePicture(dataUrl);
            userRepository.save(user);
            ra.addFlashAttribute("successMessage", "Profilbild aktualisiert.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Upload fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // ✅ Team erstellen: nimmt den eingeloggten User statt defaultUserId=1
    @PostMapping("/home/team/create")
    public String createTeam(@RequestParam String teamName, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/login";

        try {
            teamService.createTeam(teamName, userId);
            model.addAttribute("successMessage", "✅ Team erstellt!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Fehler beim Erstellen des Teams: " + e.getMessage());
        }

        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "home";
    }

    @PostMapping("/home/team/addMember")
    public String addMemberToTeam(@RequestParam Long teamId, @RequestParam Long userId, HttpSession session,
            Model model) {
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null)
            return "redirect:/login";

        try {
            teamService.addMemberToTeam(teamId, userId);
            model.addAttribute("successMessage", "✅ Member hinzugefuegt!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Fehler beim Hinzufuegen: " + e.getMessage());
        }

        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
