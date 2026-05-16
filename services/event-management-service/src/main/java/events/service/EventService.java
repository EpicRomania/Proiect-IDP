package events.service;

import events.Event;
import events.dto.EventRequest;
import events.dto.EventResponse;
import events.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        validateTimeRange(request);
        Event event = new Event(
                request.title(),
                request.description(),
                request.location(),
                request.startTime(),
                request.endTime(),
                request.organizerId()
        );
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> list() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getStartTime))
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse get(Long id) {
        return EventResponse.from(findEvent(id));
    }

    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        validateTimeRange(request);
        Event event = findEvent(id);
        event.update(
                request.title(),
                request.description(),
                request.location(),
                request.startTime(),
                request.endTime(),
                request.organizerId()
        );
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        Event event = findEvent(id);
        eventRepository.delete(event);
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    private void validateTimeRange(EventRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new BadRequestException("Event endTime must be after startTime");
        }
    }
}
