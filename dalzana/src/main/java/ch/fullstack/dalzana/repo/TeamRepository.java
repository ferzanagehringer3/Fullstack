package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> { }
