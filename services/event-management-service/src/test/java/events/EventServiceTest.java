package events;

import events.Event;
import events.dto.EventRequest;
import events.EventRepository;
import events.service.BadRequestException;
import events.service.EventService;
import events.service.NotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private final EventRepository eventRepository = mock(EventRepository.class);
    private final EventService eventService = new EventService(eventRepository);

    @Test
    void createRejectsInvalidTimeRange() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        EventRequest request = new EventRequest("Title", "Description", "Bucharest", start, start.minusHours(1), 1L);

        assertThatThrownBy(() -> eventService.create(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void listSortsByStartTime() {
        Event later = new Event("Later", "Description", "Bucharest", LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(2), 1L);
        Event earlier = new Event("Earlier", "Description", "Cluj", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 1L);
        when(eventRepository.findAll()).thenReturn(List.of(later, earlier));

        var events = eventService.list();

        assertThat(events).extracting("title").containsExactly("Earlier", "Later");
    }

    @Test
    void updateRejectsMissingEvent() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        EventRequest request = new EventRequest("Title", "Description", "Bucharest", start, start.plusHours(1), 1L);
        when(eventRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.update(10L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRejectsMissingEvent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.get(10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteRejectsMissingEvent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.delete(10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createPersistsValidEvent() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        EventRequest request = new EventRequest("Title", "Description", "Bucharest", start, start.plusHours(1), 1L);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = eventService.create(request);

        assertThat(response.title()).isEqualTo("Title");
        assertThat(response.organizerId()).isEqualTo(1L);
    }

    @Test
    void updatePersistsChangedEvent() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        Event event = new Event("Old", "Description", "Bucharest", start, start.plusHours(1), 1L);
        EventRequest request = new EventRequest("New", "Updated description", "Cluj", start.plusDays(1), start.plusDays(1).plusHours(2), 2L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = eventService.update(1L, request);

        assertThat(response.title()).isEqualTo("New");
        assertThat(response.location()).isEqualTo("Cluj");
        assertThat(response.organizerId()).isEqualTo(2L);
    }

    @Test
    void deleteRemovesExistingEvent() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        Event event = new Event("Title", "Description", "Bucharest", start, start.plusHours(1), 1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.delete(1L);

        verify(eventRepository).delete(event);
    }
}
