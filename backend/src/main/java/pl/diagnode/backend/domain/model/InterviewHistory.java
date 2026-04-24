package pl.diagnode.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.unmodifiableSortedMap;

/**
 * Long-term persistent snapshot of an interview session (mirror of {@link InterviewContext}
 * stored in Postgres once the short-lived Redis session expires).
 * <p>
 * Effectively immutable from the outside: there are no setters and collections are
 * returned as unmodifiable views. A no-args constructor and non-final fields are
 * retained only because Hibernate requires them for entity hydration.
 */
@Entity
@Table(name = "interview_history")
public class InterviewHistory {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "current_node_id")
    private UUID currentNodeId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "collected_points")
    private TreeMap<String, Integer> collectedPoints = new TreeMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answers")
    private HashMap<String, String> answers = new HashMap<>();

    @Column(name = "ai_consent_given", nullable = false)
    private boolean aiConsentGiven;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile_data")
    private HashMap<String, String> profileData = new HashMap<>();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Required by Hibernate. Not intended for application code. */
    protected InterviewHistory() {
    }

    /** Canonical constructor used by MapStruct and application code. */
    public InterviewHistory(String userId,
                            UUID currentNodeId,
                            SortedMap<String, Integer> collectedPoints,
                            Map<String, String> answers,
                            boolean aiConsentGiven,
                            Map<String, String> profileData) {
        this.userId = userId;
        this.currentNodeId = currentNodeId;
        this.collectedPoints = collectedPoints == null ? new TreeMap<>() : new TreeMap<>(collectedPoints);
        this.answers = answers == null ? new HashMap<>() : new HashMap<>(answers);
        this.aiConsentGiven = aiConsentGiven;
        this.profileData = profileData == null ? new HashMap<>() : new HashMap<>(profileData);
    }

    public String getUserId() {
        return userId;
    }

    public UUID getCurrentNodeId() {
        return currentNodeId;
    }

    public SortedMap<String, Integer> getCollectedPoints() {
        return unmodifiableSortedMap(collectedPoints);
    }

    public Map<String, String> getAnswers() {
        return Collections.unmodifiableMap(answers);
    }

    public boolean isAiConsentGiven() {
        return aiConsentGiven;
    }

    public Map<String, String> getProfileData() {
        return Collections.unmodifiableMap(profileData);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof InterviewHistory that)) return false;
        return userId != null && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
