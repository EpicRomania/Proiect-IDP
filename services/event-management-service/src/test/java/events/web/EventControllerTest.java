package events.web;

import events.dto.EventRequest;
import events.dto.EventResponse;
import events.service.BadRequestException;
import events.service.EventService;
import events.service.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(ApiExceptionHandler.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void createReturnsCreatedEvent() throws Exception {
        EventRequest request = validRequest("Title");
        when(eventService.create(request)).thenReturn(response(1L, "Title"));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void listReturnsEvents() throws Exception {
        when(eventService.list()).thenReturn(List.of(response(1L, "First"), response(2L, "Second")));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("First"));
    }

    @Test
    void getReturnsEvent() throws Exception {
        when(eventService.get(1L)).thenReturn(response(1L, "Title"));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void updateReturnsUpdatedEvent() throws Exception {
        EventRequest request = validRequest("Updated");
        when(eventService.update(1L, request)).thenReturn(response(1L, "Updated"));

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void invalidPayloadReturnsBadRequest() throws Exception {
        EventRequest request = new EventRequest("", "", "", LocalDateTime.now().minusDays(1), null, -1L);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingEventReturnsNotFound() throws Exception {
        when(eventService.get(99L)).thenThrow(new NotFoundException("Event not found"));

        mockMvc.perform(get("/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void invalidTimeRangeReturnsBadRequest() throws Exception {
        EventRequest request = validRequest("Title");
        when(eventService.create(request)).thenThrow(new BadRequestException("Event endTime must be after startTime"));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deleteMissingEventReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Event not found")).when(eventService).delete(99L);

        mockMvc.perform(delete("/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private EventRequest validRequest(String title) {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        return new EventRequest(title, "Description", "Bucharest", start, start.plusHours(2), 1L);
    }

    private EventResponse response(Long id, String title) {
        LocalDateTime start = LocalDateTime.parse("2026-06-01T10:00:00");
        return new EventResponse(
                id,
                title,
                "Description",
                "Bucharest",
                start,
                start.plusHours(2),
                1L,
                Instant.parse("2026-05-16T10:00:00Z"),
                Instant.parse("2026-05-16T10:00:00Z")
        );
    }
}
