package web.eventbookingapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import web.eventbookingapp.model.enums.EntranceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "entrances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_entrance_user_event",
                        columnNames = {"user_id", "event_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Entrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String qrCode = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntranceStatus status = EntranceStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime bookedAt = LocalDateTime.now();

    private LocalDateTime usedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;
}