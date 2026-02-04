package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_read_status", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"}))
public class MessageReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private LocalDateTime readAt = LocalDateTime.now();

    protected MessageReadStatus() {}

    public MessageReadStatus(Message message, AppUser user) {
        this.message = message;
        this.user = user;
    }

    public Long getId() { return id; }
    public Message getMessage() { return message; }
    public AppUser getUser() { return user; }
    public LocalDateTime getReadAt() { return readAt; }
}
