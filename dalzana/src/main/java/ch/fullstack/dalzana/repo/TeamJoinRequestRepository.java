package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.TeamJoinRequest;
import ch.fullstack.dalzana.model.TeamJoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
    Optional<TeamJoinRequest> findByToken(String token);
    boolean existsByTeamIdAndRequesterIdAndStatus(Long teamId, Long requesterId, TeamJoinRequestStatus status);
    Optional<TeamJoinRequest> findTopByTeamIdAndRequesterIdOrderByCreatedAtDesc(Long teamId, Long requesterId);
}
