package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByRequestId(Long requestId);
    
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.id = :userId")
    List<Team> findTeamsByMemberId(@Param("userId") Long userId);
}
