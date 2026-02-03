package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.Message;
import ch.fullstack.dalzana.repo.AppUserRepository;
import ch.fullstack.dalzana.repo.MessageRepository;
import ch.fullstack.dalzana.repo.TeamRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageRestController {

    private final MessageRepository messageRepo;
    private final TeamRepository teamRepo;
    private final AppUserRepository userRepo;

    public MessageRestController(MessageRepository messageRepo, TeamRepository teamRepo, AppUserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.teamRepo = teamRepo;
        this.userRepo = userRepo;
    }

    // ✅ GET /api/teams/{teamId}/messages
    @GetMapping("/teams/{teamId}/messages")
    public List<MessageDto> list(@PathVariable Long teamId) {
        return messageRepo.findByTeamIdOrderByCreatedAtAsc(teamId).stream()
                .map(MessageDto::from)
                .toList();
    }

    // ✅ POST /api/teams/{teamId}/messages
    @PostMapping("/teams/{teamId}/messages")
    public MessageDto create(@PathVariable Long teamId, @RequestBody CreateMessageRequest body) {
        var team = teamRepo.findById(teamId).orElseThrow();
        var sender = userRepo.findById(body.senderId()).orElseThrow();

        Message saved = messageRepo.save(new Message(team, sender, body.content()));
        return MessageDto.from(saved);
    }

    // ===== DTOs =====
    public record CreateMessageRequest(Long senderId, String content) {}

    public record MessageDto(
            Long id,
            Long teamId,
            Long senderId,
            String senderName,
            String content,
            LocalDateTime createdAt
    ) {
        static MessageDto from(Message m) {
            return new MessageDto(
                    m.getId(),
                    m.getTeam().getId(),
                    m.getSender().getId(),
                    m.getSender().getName(),
                    m.getContent(),
                    m.getCreatedAt()
            );
        }
    }
}
