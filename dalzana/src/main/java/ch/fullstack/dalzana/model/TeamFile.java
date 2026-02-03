package ch.fullstack.dalzana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_files")
public class TeamFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="team_id")
    private Team team;

    @Column(nullable=false)
    private String originalName;

    @Column(nullable=false, unique=true)
    private String storedName;

    @Column(nullable=false)
    private long sizeBytes;

    @Lob
    @Column(nullable=false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(nullable=false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    protected TeamFile() {}

    public TeamFile(Team team, String originalName, String storedName, long sizeBytes, byte[] fileData) {
        this.team = team;
        this.originalName = originalName;
        this.storedName = storedName;
        this.sizeBytes = sizeBytes;
        this.fileData = fileData;
    }

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public String getOriginalName() { return originalName; }
    public String getStoredName() { return storedName; }
    public long getSizeBytes() { return sizeBytes; }
    public byte[] getFileData() { return fileData; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}
