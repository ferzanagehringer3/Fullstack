package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.RequestStatus;
import ch.fullstack.dalzana.model.TeamJoinRequest;
import ch.fullstack.dalzana.model.TeamJoinRequestStatus;
import ch.fullstack.dalzana.model.AppUser;
import ch.fullstack.dalzana.repo.AppUserRepository;
import ch.fullstack.dalzana.repo.RequestRepository;
import ch.fullstack.dalzana.repo.TeamJoinRequestRepository;
import ch.fullstack.dalzana.service.EmailService;
import ch.fullstack.dalzana.service.RequestService;
import ch.fullstack.dalzana.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final AppUserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final TeamJoinRequestRepository joinRequestRepository;
    private final EmailService emailService;
    
    @Value("${app.base-url}")
    private String appBaseUrl;

    @ModelAttribute
    public void addCommonAttributes(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        
        // Immer den aktuellen User aus der DB holen
        if (userId != null) {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                AppUser user = userOpt.get();
                model.addAttribute("userName", user.getName());
                model.addAttribute("currentUserRole", user.getRole().name());
                
                // Profilbild hinzufügen (ist bereits als String gespeichert)
                if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                    model.addAttribute("userProfilePicture", user.getProfilePicture());
                }
            }
        }
    }

    public TeamController(TeamService teamService,
                          AppUserRepository userRepository,
                          RequestRepository requestRepository,
                          RequestService requestService,
                          TeamJoinRequestRepository joinRequestRepository,
                          EmailService emailService) {
        this.teamService = teamService;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.requestService = requestService;
        this.joinRequestRepository = joinRequestRepository;
        this.emailService = emailService;
    }

    @GetMapping("/create")
    public String createPage(@RequestParam(required = false) Long requestId, Model model, HttpSession session, RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams erstellen.");
            return "redirect:/home";
        }
        
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("requests", requestRepository.findAll());
        model.addAttribute("selectedRequestId", requestId);
        
        if (requestId != null) {
            requestRepository.findById(requestId).ifPresent(request -> {
                model.addAttribute("prefilledDescription", request.getDescription());
            });
        }
        
        return "team-create";
    }

    @PostMapping("/create")
    public String createTeam(@RequestParam String teamName,
                            @RequestParam Long requestId,
                            @RequestParam(required = false) String requestDescription,
                            @RequestParam(required = false) Long[] userIds,
                            Model model,
                            HttpSession session,
                            RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams erstellen.");
            return "redirect:/home";
        }
        
        try {
            Long defaultUserId = 1L;
            if (!userRepository.existsById(defaultUserId)) {
                model.addAttribute("errorMessage", "❌ Kein User gefunden.");
                model.addAttribute("users", userRepository.findAll());
                model.addAttribute("requests", requestRepository.findAll());
                return "team-create";
            }
            
            var team = teamService.createTeam(teamName, defaultUserId, requestId, requestDescription);
            
            if (userIds != null) {
                for (Long userId : userIds) {
                    if (userId != null && !userId.equals(defaultUserId)) {
                        teamService.addMemberToTeam(team.getId(), userId);
                    }
                }
            }
            
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Fehler: " + e.getMessage());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("requests", requestRepository.findAll());
            return "team-create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes ra) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            ra.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams bearbeiten.");
            return "redirect:/home";
        }
        
        var team = teamService.findById(id);
        Set<Long> memberIds = team.getMembers().stream()
                .map(m -> m.getId())
                .collect(Collectors.toSet());

        model.addAttribute("team", team);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("memberIds", memberIds);
        return "team-edit";
    }

    @PostMapping("/{id}/edit")
    public String editTeam(@PathVariable Long id,
                           @RequestParam(required = false) String teamName,
                           @RequestParam(required = false) Long[] userIds,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams bearbeiten.");
            return "redirect:/home";
        }
        
        try {
            teamService.updateTeamName(id, teamName);
            if (userIds != null) {
                for (Long userId : userIds) {
                    if (userId != null) {
                        teamService.addMemberToTeam(id, userId);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "✅ Team aktualisiert.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Fehler beim Speichern: " + e.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/{id}/delete")
    public String deleteTeam(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MANAGER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Nur Manager können Teams löschen.");
            return "redirect:/home";
        }
        
        try {
            teamService.deleteTeam(id);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Team gelöscht.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Löschen fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        var team = teamService.findById(id);
        Long currentUserId = (Long) session.getAttribute("userId");

        boolean isMember = false;
        boolean joinRequestPending = false;
        TeamJoinRequestStatus latestJoinRequestStatus = null;
        if (currentUserId != null) {
            isMember = team.getMembers().stream().anyMatch(m -> m.getId().equals(currentUserId));
            joinRequestPending = joinRequestRepository.existsByTeamIdAndRequesterIdAndStatus(
                    team.getId(), currentUserId, TeamJoinRequestStatus.PENDING);
            latestJoinRequestStatus = joinRequestRepository
                    .findTopByTeamIdAndRequesterIdOrderByCreatedAtDesc(team.getId(), currentUserId)
                    .map(TeamJoinRequest::getStatus)
                    .orElse(null);
        }

        model.addAttribute("team", team);
        model.addAttribute("currentUserRole", session.getAttribute("userRole"));
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("isMember", isMember);
        model.addAttribute("joinRequestPending", joinRequestPending);
        model.addAttribute("joinRequestStatus", latestJoinRequestStatus);
        return "team-detail";
    }

    @PostMapping("/{teamId}/join-request")
    public String requestJoin(@PathVariable Long teamId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest httpRequest) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bitte zuerst einloggen.");
            return "redirect:/login";
        }

        var team = teamService.findById(teamId);
        var requester = userRepository.findById(userId).orElseThrow();

        boolean isMember = team.getMembers().stream().anyMatch(m -> m.getId().equals(userId));
        if (isMember) {
            redirectAttributes.addFlashAttribute("errorMessage", "Du bist bereits im Team.");
            return "redirect:/teams/" + teamId;
        }

        boolean alreadyPending = joinRequestRepository.existsByTeamIdAndRequesterIdAndStatus(
                teamId, userId, TeamJoinRequestStatus.PENDING);
        if (alreadyPending) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deine Beitrittsanfrage ist bereits offen.");
            return "redirect:/teams/" + teamId;
        }

        String token = UUID.randomUUID().toString();
        TeamJoinRequest joinRequest = new TeamJoinRequest(team, requester, token);
        joinRequestRepository.save(joinRequest);

        String approveLink = appBaseUrl + "/teams/join-requests/" + token + "/approve";
        String rejectLink = appBaseUrl + "/teams/join-requests/" + token + "/reject";

        String subject = "Beitrittsanfrage für Team: " + team.getName();
        String htmlTemplate = 
            "<html><body style=\"font-family: Arial, sans-serif; color: #333;\">" +
            "<p>Hallo %s,</p>" +
            "<p>%s möchte dem Team <strong>'%s'</strong> beitreten.</p>" +
            "<div style=\"margin: 30px 0; text-align: center;\">" +
            "<a href=\"%s\" style=\"display: inline-block; padding: 12px 30px; margin-right: 10px; background-color: #28a745; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;\">Zustimmen</a>" +
            "<a href=\"%s\" style=\"display: inline-block; padding: 12px 30px; background-color: #dc3545; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;\">Ablehnen</a>" +
            "</div>" +
            "<p style=\"color: #666; font-size: 12px; margin-top: 30px;\">Viele Grüsse,<br>Dein DalZana Team</p>" +
            "</body></html>";

        team.getMembers().forEach(member -> {
            String body = String.format(htmlTemplate, member.getName(), requester.getName(), team.getName(), approveLink, rejectLink);
            emailService.sendHtmlNotification(member.getEmail(), subject, body);
        });

        redirectAttributes.addFlashAttribute("successMessage", "Beitrittsanfrage wurde gesendet.");
        return "redirect:/teams/" + teamId;
    }

    @GetMapping("/join-requests/{token}/approve")
    public String approveJoinRequest(@PathVariable String token,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bitte zuerst einloggen.");
            return "redirect:/login";
        }

        var joinRequestOpt = joinRequestRepository.findByToken(token);
        if (joinRequestOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anfrage nicht gefunden.");
            return "redirect:/home";
        }

        var joinRequest = joinRequestOpt.get();
        var team = joinRequest.getTeam();

        boolean isMember = team.getMembers().stream().anyMatch(m -> m.getId().equals(userId));
        if (!isMember) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nur Team-Mitglieder dürfen entscheiden.");
            return "redirect:/home";
        }

        if (joinRequest.getStatus() != TeamJoinRequestStatus.PENDING) {
            redirectAttributes.addFlashAttribute("errorMessage", "Diese Anfrage wurde bereits bearbeitet.");
            return "redirect:/teams/" + team.getId();
        }

        teamService.addMemberToTeam(team.getId(), joinRequest.getRequester().getId());
        joinRequest.setStatus(TeamJoinRequestStatus.APPROVED);
        joinRequest.setRespondedAt(LocalDateTime.now());
        joinRequestRepository.save(joinRequest);

        String subject = "Team-Beitritt bestätigt";
        String htmlBody = 
            "<html><body style=\"font-family: Arial, sans-serif; color: #333;\">" +
            "<p>Hallo " + joinRequest.getRequester().getName() + ",</p>" +
            "<p>Deine Anfrage für das Team <strong>'" + team.getName() + "'</strong> wurde angenommen.</p>" +
            "<p style=\"color: #666; font-size: 12px; margin-top: 30px;\">Viele Grüsse,<br>Dein DalZana Team</p>" +
            "</body></html>";
        emailService.sendHtmlNotification(joinRequest.getRequester().getEmail(), subject, htmlBody);

        redirectAttributes.addFlashAttribute("successMessage", "Anfrage bestätigt.");
        return "redirect:/teams/" + team.getId();
    }

    @GetMapping("/join-requests/{token}/reject")
    public String rejectJoinRequest(@PathVariable String token,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bitte zuerst einloggen.");
            return "redirect:/login";
        }

        var joinRequestOpt = joinRequestRepository.findByToken(token);
        if (joinRequestOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anfrage nicht gefunden.");
            return "redirect:/home";
        }

        var joinRequest = joinRequestOpt.get();
        var team = joinRequest.getTeam();

        boolean isMember = team.getMembers().stream().anyMatch(m -> m.getId().equals(userId));
        if (!isMember) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nur Team-Mitglieder dürfen entscheiden.");
            return "redirect:/home";
        }

        if (joinRequest.getStatus() != TeamJoinRequestStatus.PENDING) {
            redirectAttributes.addFlashAttribute("errorMessage", "Diese Anfrage wurde bereits bearbeitet.");
            return "redirect:/teams/" + team.getId();
        }

        joinRequest.setStatus(TeamJoinRequestStatus.REJECTED);
        joinRequest.setRespondedAt(LocalDateTime.now());
        joinRequestRepository.save(joinRequest);

        String subject = "Team-Beitritt abgelehnt";
        String htmlBody = 
            "<html><body style=\"font-family: Arial, sans-serif; color: #333;\">" +
            "<p>Hallo " + joinRequest.getRequester().getName() + ",</p>" +
            "<p>Deine Anfrage für das Team <strong>'" + team.getName() + "'</strong> wurde abgelehnt.</p>" +
            "<p style=\"color: #666; font-size: 12px; margin-top: 30px;\">Viele Grüsse,<br>Dein DalZana Team</p>" +
            "</body></html>";
        emailService.sendHtmlNotification(joinRequest.getRequester().getEmail(), subject, htmlBody);

        redirectAttributes.addFlashAttribute("successMessage", "Anfrage abgelehnt.");
        return "redirect:/teams/" + team.getId();
    }

    @PostMapping("/{teamId}/request/status")
    public String updateRequestStatus(@PathVariable Long teamId,
                                      @RequestParam RequestStatus status,
                                      RedirectAttributes redirectAttributes) {
        try {
            var team = teamService.findById(teamId);
            if (team.getRequest() != null) {
                var request = team.getRequest();
                request.setStatus(status);
                requestService.save(request);
                redirectAttributes.addFlashAttribute("successMessage", "✅ Status aktualisiert.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Kein Request zugeordnet.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Fehler beim Aktualisieren: " + e.getMessage());
        }
        return "redirect:/teams/" + teamId;
    }
}
