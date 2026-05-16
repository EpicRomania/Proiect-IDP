package events.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotBlank @Size(max = 200) String location,
        @NotNull @Future LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotNull @Positive Long organizerId
) {
}
