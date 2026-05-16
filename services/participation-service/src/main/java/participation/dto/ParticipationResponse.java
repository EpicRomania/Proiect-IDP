package participation.dto;

import participation.Participation;

import java.time.Instant;

public record ParticipationResponse(Long id, Long eventId, Long userId, Instant registeredAt) {

    public static ParticipationResponse from(Participation participation) {
        return new ParticipationResponse(
                participation.getId(),
                participation.getEventId(),
                participation.getUserId(),
                participation.getRegisteredAt()
        );
    }
}
