package participation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "participations",
        uniqueConstraints = @UniqueConstraint(name = "uk_participation_event_user", columnNames = {"event_id", "user_id"})
)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Instant registeredAt;

    protected Participation() {
    }

    public Participation(Long eventId, Long userId, Instant registeredAt) {
        this.eventId = eventId;
        this.userId = userId;
        this.registeredAt = registeredAt;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
