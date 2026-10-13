package web.eventbookingapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.eventbookingapp.model.Entrance;
import web.eventbookingapp.model.EventEntity;
import web.eventbookingapp.model.UserEntity;
import web.eventbookingapp.model.enums.EntranceStatus;
import web.eventbookingapp.repository.EntranceRepository;
import web.eventbookingapp.repository.EventRepository;
import web.eventbookingapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EntranceService {

    private final EntranceRepository entranceRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Entrance bookEvent(Long userId, Long eventId) {

        EventEntity event = eventRepository
                .findByIdForUpdate(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Event not found"
                ));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"
                ));

        if (!event.getStartDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "This event has already started"
            );
        }

        if (entranceRepository.existsByUser_IdAndEvent_Id(
                userId, eventId)) {
            throw new IllegalStateException(
                    "User already has a ticket for this event"
            );
        }

        long occupied = entranceRepository
                .countByEvent_IdAndStatusIn(
                        eventId,
                        List.of(
                                EntranceStatus.ACTIVE,
                                EntranceStatus.USED
                        )
                );

        if (occupied >= event.getCapacity()) {
            throw new IllegalStateException(
                    "No available slots for this event"
            );
        }

        Entrance entrance = new Entrance();
        entrance.setUser(user);
        entrance.setEvent(event);
        entrance.setStatus(EntranceStatus.ACTIVE);

        // qrCode ma domyślną wartość UUID
        return entranceRepository.save(entrance);
    }

    public Entrance getTicket(Long ticketId) {
        return entranceRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ticket not found"
                ));
    }

    public List<Entrance> getUserTickets(Long userId) {
        return entranceRepository.findByUser_Id(userId);
    }

    public List<Entrance> getActiveTickets(Long userId) {
        return entranceRepository.findByUser_IdAndStatus(
                userId,
                EntranceStatus.ACTIVE
        );
    }

    public List<Entrance> getEventTickets(Long eventId) {
        return entranceRepository.findByEvent_Id(eventId);
    }

    public Entrance cancelTicket(Long ticketId, Long userId) {
        Entrance entrance = getTicket(ticketId);

        if (!entrance.getUser().getId().equals(userId)) {
            throw new IllegalStateException(
                    "You cannot cancel another user's ticket"
            );
        }

        if (entrance.getStatus() != EntranceStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active tickets can be cancelled"
            );
        }

        if (!entrance.getEvent().getStartDate()
                .isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "The event has already started"
            );
        }

        entrance.setStatus(EntranceStatus.CANCELLED);

        return entranceRepository.save(entrance);
    }

    public Entrance validateQrCode(String qrCode) {
        Entrance entrance = entranceRepository
                .findByQrCode(qrCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid QR code"
                ));

        if (entrance.getStatus() != EntranceStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Ticket is cancelled or has already been used"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        EventEntity event = entrance.getEvent();

        if (now.isBefore(event.getStartDate())
                || !now.isBefore(event.getEndDate())) {
            throw new IllegalStateException(
                    "Ticket cannot be used outside the event time"
            );
        }

        entrance.setStatus(EntranceStatus.USED);
        entrance.setUsedAt(now);

        return entranceRepository.save(entrance);
    }
}