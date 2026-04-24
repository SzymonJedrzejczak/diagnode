package pl.diagnode.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;


/**
 * Long-term persistent snapshot of an interview session (mirror of {@link InterviewContext}
 * stored in Postgres once the short-lived Redis session expires).
 * <p>
 * Must remain a plain JavaBean (not a record) because Hibernate requires a no-args
 * constructor and mutable fields.
 */
@Entity
@Table(name = "interview_history")
public class InterviewHistory {

    /** Bot-provided user identifier (primary key). */
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    /** Progress pointer — current position in the interview graph. */
    @Column(name = "current_node_id")
    private UUID currentNodeId;

    /** Aggregated diagnostic points: category → total points. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "collected_points")
    private TreeMap<String, Integer> collectedPoints = new TreeMap<>();

    /** Log of given answers: node id → answer text. Basis for later PDF generation. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answers")
    private HashMap<String, String> answers = new HashMap<>();

    /** Legal flag — whether the user consents to LLM process of their text. */
    @Column(name = "ai_consent_given", nullable = false)
    private boolean aiConsentGiven;

    /** Dynamic user profile (e.g. {@code userName -> Marek}). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile_data")
    private HashMap<String, String> profileData = new HashMap<>();

    /** Timestamp of the last update — maintained automatically by Hibernate. */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public InterviewHistory() {
        // required by JPA
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(UUID currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public SortedMap<String, Integer> getCollectedPoints() {
        return collectedPoints;
    }

    public void setCollectedPoints(SortedMap<String, Integer> collectedPoints) {
        this.collectedPoints = collectedPoints == null ? new TreeMap<>() : new TreeMap<>(collectedPoints);
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers == null ? new HashMap<>() : new HashMap<>(answers);
    }

    public boolean isAiConsentGiven() {
        return aiConsentGiven;
    }

    public void setAiConsentGiven(boolean aiConsentGiven) {
        this.aiConsentGiven = aiConsentGiven;
    }

    public Map<String, String> getProfileData() {
        return profileData;
    }

    public void setProfileData(Map<String, String> profileData) {
        this.profileData = profileData == null ? new HashMap<>() : new HashMap<>(profileData);
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
