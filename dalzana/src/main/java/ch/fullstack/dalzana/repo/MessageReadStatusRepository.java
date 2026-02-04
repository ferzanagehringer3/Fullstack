package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.MessageReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
    
    boolean existsByMessageIdAndUserId(Long messageId, Long userId);
    
    List<MessageReadStatus> findByMessageId(Long messageId);
    
    @Query("SELECT m.id FROM Message m WHERE m.team.id = :teamId " +
           "AND m.sender.id != :userId " +
           "AND NOT EXISTS (SELECT 1 FROM MessageReadStatus mrs WHERE mrs.message.id = m.id AND mrs.user.id = :userId)")
    List<Long> findUnreadMessageIdsByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.team.id = :teamId " +
           "AND m.sender.id != :userId " +
           "AND NOT EXISTS (SELECT 1 FROM MessageReadStatus mrs WHERE mrs.message.id = m.id AND mrs.user.id = :userId)")
    long countUnreadMessagesByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);
}
