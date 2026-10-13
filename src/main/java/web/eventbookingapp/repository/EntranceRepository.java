package web.eventbookingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.eventbookingapp.model.Entrance;
import web.eventbookingapp.model.enums.EntranceStatus;

import java.util.List;
import java.util.Optional;

public interface EntranceRepository
        extends JpaRepository<Entrance, Long> {

    Optional<Entrance> findByQrCode(String qrCode);

    List<Entrance> findByUser_Id(Long userId);

    List<Entrance> findByUser_IdAndStatus(
            Long userId,
            EntranceStatus status
    );

    List<Entrance> findByEvent_Id(Long eventId);

    long countByEvent_IdAndStatusIn(
            Long eventId,
            List<EntranceStatus> statuses
    );

    boolean existsByUser_IdAndEvent_Id(
            Long userId,
            Long eventId
    );
}