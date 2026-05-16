package participation.service;

import participation.Participation;
import participation.dto.ParticipationRequest;
import participation.dto.ParticipationResponse;
import participation.ParticipationRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final JdbcTemplate jdbcTemplate;

    public ParticipationService(ParticipationRepository participationRepository, JdbcTemplate jdbcTemplate) {
        this.participationRepository = participationRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public ParticipationResponse register(ParticipationRequest request) {
        if (!eventExists(request.eventId())) {
            throw new NotFoundException("Event not found");
        }
        if (participationRepository.existsByEventIdAndUserId(request.eventId(), request.userId())) {
            throw new ConflictException("User is already registered for this event");
        }

        Participation participation = new Participation(request.eventId(), request.userId(), Instant.now());
        return ParticipationResponse.from(participationRepository.save(participation));
    }

    @Transactional
    public void withdraw(Long eventId, Long userId) {
        Participation participation = participationRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Participation not found"));
        participationRepository.delete(participation);
    }

    @Transactional(readOnly = true)
    public List<ParticipationResponse> participantsForEvent(Long eventId) {
        if (!eventExists(eventId)) {
            throw new NotFoundException("Event not found");
        }
        return participationRepository.findByEventIdOrderByRegisteredAtAsc(eventId).stream()
                .map(ParticipationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParticipationResponse> eventsForUser(Long userId) {
        return participationRepository.findByUserIdOrderByRegisteredAtDesc(userId).stream()
                .map(ParticipationResponse::from)
                .toList();
    }

    private boolean eventExists(Long eventId) {
        try {
            Integer count = jdbcTemplate.queryForObject("select count(1) from events where id = ?", Integer.class, eventId);
            return count != null && count > 0;
        } catch (RuntimeException exception) {
            throw new BadRequestException("Event data is not available");
        }
    }
}
