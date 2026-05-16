package participation.web;

import participation.dto.ParticipationRequest;
import participation.dto.ParticipationResponse;
import participation.service.ParticipationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationResponse register(@Valid @RequestBody ParticipationRequest request) {
        return participationService.register(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@RequestParam @Positive Long eventId, @RequestParam @Positive Long userId) {
        participationService.withdraw(eventId, userId);
    }

    @GetMapping("/events/{eventId}")
    public List<ParticipationResponse> participantsForEvent(@PathVariable @Positive Long eventId) {
        return participationService.participantsForEvent(eventId);
    }

    @GetMapping("/users/{userId}")
    public List<ParticipationResponse> eventsForUser(@PathVariable @Positive Long userId) {
        return participationService.eventsForUser(userId);
    }
}
