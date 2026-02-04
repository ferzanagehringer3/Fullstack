package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_join_requests")
public class TeamJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_user_id")
    private AppUser requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamJoinRequestStatus status = TeamJoinRequestStatus.PENDING;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime respondedAt;

    protected TeamJoinRequest() {}

    public TeamJoinRequest(Team team, AppUser requester, String token) {
        this.team = team;
        this.requester = requester;
        this.token = token;
    }

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public AppUser getRequester() { return requester; }
    public TeamJoinRequestStatus getStatus() { return status; }
    public String getToken() { return token; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getRespondedAt() { return respondedAt; }

    public void setStatus(TeamJoinRequestStatus status) { this.status = status; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}
