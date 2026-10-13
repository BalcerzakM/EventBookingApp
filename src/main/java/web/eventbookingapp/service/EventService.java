package web.eventbookingapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.eventbookingapp.model.EventEntity;
import web.eventbookingapp.model.enums.EntranceStatus;
import web.eventbookingapp.repository.EntranceRepository;
import web.eventbookingapp.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EntranceRepository entranceRepository;

    public EventEntity createEvent(EventEntity event) {
        validateDates(event);

        if (event.getCapacity() < 1) {
            throw new IllegalArgumentException(
                    "Capacity must be at least 1"
            );
        }

        return eventRepository.save(event);
    }

    public EventEntity getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Event not found: " + eventId
                ));
    }

    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }

    public int getAvailableSlots(Long eventId) {
        EventEntity event = getEvent(eventId);

        long occupied = entranceRepository
                .countByEvent_IdAndStatusIn(
                        eventId,
                        List.of(
                                EntranceStatus.ACTIVE,
                                EntranceStatus.USED
                        )
                );

        return Math.max(
                0,
                event.getCapacity() - (int) occupied
        );
    }

    public EventEntity updateEvent(
            Long eventId,
            EventEntity updated
    ) {
        EventEntity event = eventRepository.findByIdForUpdate(
                eventId
        ).orElseThrow(() -> new IllegalArgumentException(
                "Event not found: " + eventId
        ));

        validateDates(updated);

        long occupied = entranceRepository
                .countByEvent_IdAndStatusIn(
                        eventId,
                        List.of(
                                EntranceStatus.ACTIVE,
                                EntranceStatus.USED
                        )
                );

        if (updated.getCapacity() < occupied) {
            throw new IllegalStateException(
                    "Capacity cannot be lower than the number "
                            + "of existing tickets"
            );
        }

        if (updated.getCapacity() < 1) {
            throw new IllegalArgumentException(
                    "Capacity must be at least 1"
            );
        }

        event.setName(updated.getName());
        event.setDescription(updated.getDescription());
        event.setLocation(updated.getLocation());
        event.setCapacity(updated.getCapacity());
        event.setStartDate(updated.getStartDate());
        event.setEndDate(updated.getEndDate());

        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        EventEntity event = getEvent(eventId);

        if (entranceRepository.findByEvent_Id(eventId)
                .stream()
                .anyMatch(e ->
                        e.getStatus() != EntranceStatus.CANCELLED
                )) {
            throw new IllegalStateException(
                    "Cannot delete an event with active or used tickets"
            );
        }

        eventRepository.delete(event);
    }

    private void validateDates(EventEntity event) {
        if (event.getStartDate() == null
                || event.getEndDate() == null
                || !event.getEndDate().isAfter(event.getStartDate())) {
            throw new IllegalArgumentException(
                    "End date must be after start date"
            );
        }
    }
}