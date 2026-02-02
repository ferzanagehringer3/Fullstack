package ch.fullstack.dalzana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    protected Skill() {}

    public Skill(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
}
