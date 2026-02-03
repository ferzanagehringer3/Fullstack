package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name="requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private RequestStatus status = RequestStatus.NEW;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "request_required_skills", joinColumns = @JoinColumn(name="request_id"))
    @Column(name = "skill")
    private Set<Skill> requiredSkills = EnumSet.noneOf(Skill.class);

    protected Request() {}

    public Request(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public RequestStatus getStatus() { return status; }
    public Set<Skill> getRequiredSkills() { return requiredSkills; }

    public void addRequiredSkill(Skill skill) { this.requiredSkills.add(skill); }
    public void removeRequiredSkill(Skill skill) { this.requiredSkills.remove(skill); }
    public void clearRequiredSkills() { this.requiredSkills.clear(); }
    public void setStatus(RequestStatus status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setTitle(String title) { this.title = title; }
}

