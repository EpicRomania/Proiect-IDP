package participation;

import participation.Participation;
import participation.dto.ParticipationRequest;
import participation.ParticipationRepository;
import participation.service.ConflictException;
import participation.service.NotFoundException;
import participation.service.ParticipationService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParticipationServiceTest {

    private final ParticipationRepository participationRepository = mock(ParticipationRepository.class);
    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final ParticipationService participationService = new ParticipationService(participationRepository, jdbcTemplate);

    @Test
    void registerRejectsMissingEvent() {
        when(jdbcTemplate.queryForObject(eq("select count(1) from events where id = ?"), eq(Integer.class), eq(99L)))
                .thenReturn(0);

        assertThatThrownBy(() -> participationService.register(new ParticipationRequest(99L, 1L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void registerRejectsDuplicateParticipation() {
        when(jdbcTemplate.queryForObject(eq("select count(1) from events where id = ?"), eq(Integer.class), eq(1L)))
                .thenReturn(1);
        when(participationRepository.existsByEventIdAndUserId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> participationService.register(new ParticipationRequest(1L, 2L)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void registerCreatesParticipation() {
        when(jdbcTemplate.queryForObject(eq("select count(1) from events where id = ?"), eq(Integer.class), eq(1L)))
                .thenReturn(1);
        when(participationRepository.save(any(Participation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = participationService.register(new ParticipationRequest(1L, 2L));

        assertThat(response.eventId()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(2L);
    }

    @Test
    void withdrawRejectsMissingParticipation() {
        when(participationRepository.findByEventIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participationService.withdraw(1L, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void participantsForEventReturnsRegisteredUsers() {
        when(jdbcTemplate.queryForObject(eq("select count(1) from events where id = ?"), eq(Integer.class), eq(1L)))
                .thenReturn(1);
        when(participationRepository.findByEventIdOrderByRegisteredAtAsc(1L))
                .thenReturn(List.of(new Participation(1L, 2L, Instant.now())));

        var participants = participationService.participantsForEvent(1L);

        assertThat(participants).hasSize(1);
        assertThat(participants.getFirst().userId()).isEqualTo(2L);
    }
}
