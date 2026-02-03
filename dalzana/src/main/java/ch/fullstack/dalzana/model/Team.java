package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TeamStatus status = TeamStatus.PROPOSED;

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_user_id")
    private AppUser createdBy;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> members = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    protected Team() {}

    public Team(String name, Request request, AppUser createdBy) {
        this.name = name;
        this.request = request;
        this.createdBy = createdBy;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public TeamStatus getStatus() { return status; }
    public Request getRequest() { return request; }
    public AppUser getCreatedBy() { return createdBy; }
    public Set<AppUser> getMembers() { return members; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void addMember(AppUser user) { this.members.add(user); }
    public void setName(String name) { this.name = name; }
}
