package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private role role;

    @Column(columnDefinition = "LONGTEXT")
    private String profilePicture;

    @ManyToMany
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    protected AppUser() {}

    public AppUser(String name, String email, role role) {
        this.name = name; 
        this.email = email; 
        this.role = role;
        this.profilePicture = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' fill='%23e0e0e0'/%3E%3Ccircle cx='50' cy='35' r='15' fill='%23999'/%3E%3Cpath d='M30 70 Q30 50 50 50 Q70 50 70 70' fill='%23999'/%3E%3C/svg%3E";
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public role getRole() { return role; }
    public String getProfilePicture() { return profilePicture; }
    public Set<Skill> getSkills() { return skills; }

    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public void addSkill(Skill skill) { this.skills.add(skill); }
}
