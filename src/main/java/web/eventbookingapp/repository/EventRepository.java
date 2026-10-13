package web.eventbookingapp.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.eventbookingapp.model.EventEntity;

import java.util.Optional;

public interface EventRepository
        extends JpaRepository<EventEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventEntity e WHERE e.id = :id")
    Optional<EventEntity> findByIdForUpdate(
            @Param("id") Long id
    );
}