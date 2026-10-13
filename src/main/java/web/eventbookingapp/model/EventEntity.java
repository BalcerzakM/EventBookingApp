package web.eventbookingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Column(nullable = false)
    private String location;

    @Min(1)
    @Column(nullable = false)
    private int capacity;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Entrance> entrances = new ArrayList<>();
}