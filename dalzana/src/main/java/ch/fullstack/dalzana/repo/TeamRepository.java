package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByRequestId(Long requestId);
}
