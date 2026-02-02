package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.util.HashSet;
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

    @ManyToMany
    @JoinTable(
            name = "request_required_skills",
            joinColumns = @JoinColumn(name="request_id"),
            inverseJoinColumns = @JoinColumn(name="skill_id")
    )
    private Set<Skill> requiredSkills = new HashSet<>();

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
}
