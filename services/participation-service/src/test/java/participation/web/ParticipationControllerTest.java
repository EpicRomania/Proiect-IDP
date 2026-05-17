package participation.web;

import participation.dto.ParticipationRequest;
import participation.dto.ParticipationResponse;
import participation.service.BadRequestException;
import participation.service.ConflictException;
import participation.service.NotFoundException;
import participation.service.ParticipationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipationController.class)
@Import(ApiExceptionHandler.class)
class ParticipationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParticipationService participationService;

    @Test
    void registerReturnsCreatedParticipation() throws Exception {
        ParticipationRequest request = new ParticipationRequest(1L, 2L);
        when(participationService.register(request)).thenReturn(response(10L, 1L, 2L));

        mockMvc.perform(post("/participations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.eventId").value(1))
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void withdrawReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/participations")
                        .param("eventId", "1")
                        .param("userId", "2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void participantsForEventReturnsData() throws Exception {
        when(participationService.participantsForEvent(1L)).thenReturn(List.of(response(10L, 1L, 2L), response(11L, 1L, 3L)));

        mockMvc.perform(get("/participations/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].userId").value(3));
    }

    @Test
    void eventsForUserReturnsData() throws Exception {
        when(participationService.eventsForUser(2L)).thenReturn(List.of(response(10L, 1L, 2L), response(11L, 3L, 2L)));

        mockMvc.perform(get("/participations/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].eventId").value(3));
    }

    @Test
    void invalidBodyReturnsBadRequest() throws Exception {
        ParticipationRequest request = new ParticipationRequest(-1L, null);

        mockMvc.perform(post("/participations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void duplicateParticipationReturnsConflict() throws Exception {
        ParticipationRequest request = new ParticipationRequest(1L, 2L);
        when(participationService.register(request)).thenThrow(new ConflictException("User is already registered for this event"));

        mockMvc.perform(post("/participations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void missingParticipationReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Participation not found")).when(participationService).withdraw(1L, 2L);

        mockMvc.perform(delete("/participations")
                        .param("eventId", "1")
                        .param("userId", "2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void missingEventReturnsNotFound() throws Exception {
        when(participationService.participantsForEvent(99L)).thenThrow(new NotFoundException("Event not found"));

        mockMvc.perform(get("/participations/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void unavailableEventDataReturnsBadRequest() throws Exception {
        ParticipationRequest request = new ParticipationRequest(1L, 2L);
        when(participationService.register(request)).thenThrow(new BadRequestException("Event data is not available"));

        mockMvc.perform(post("/participations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private ParticipationResponse response(Long id, Long eventId, Long userId) {
        return new ParticipationResponse(id, eventId, userId, Instant.parse("2026-05-16T10:00:00Z"));
    }
}
