package participation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ParticipationRequest(
        @NotNull @Positive Long eventId,
        @NotNull @Positive Long userId
) {
}
