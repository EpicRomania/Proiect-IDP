package participation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<Participation> findByEventIdAndUserId(Long eventId, Long userId);

    List<Participation> findByEventIdOrderByRegisteredAtAsc(Long eventId);

    List<Participation> findByUserIdOrderByRegisteredAtDesc(Long userId);
}
