package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.TeamFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamFileRepository extends JpaRepository<TeamFile, Long> {
    List<TeamFile> findByTeamIdOrderByUploadedAtDesc(Long teamId);
}
