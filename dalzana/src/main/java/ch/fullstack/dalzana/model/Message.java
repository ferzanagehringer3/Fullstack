package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_user_id")
    private AppUser sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Message() {}

    public Message(Team team, AppUser sender, String content) {
        this.team = team;
        this.sender = sender;
        this.content = content;
    }

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public AppUser getSender() { return sender; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
