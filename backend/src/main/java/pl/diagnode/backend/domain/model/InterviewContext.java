package pl.diagnode.backend.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.*;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSortedMap;

/**
 * Short-lived interview session kept in Redis. Immutable by design — every state
 * transition produces a new instance via {@link Builder}.
 */
@RedisHash(value = "interview_sessions", timeToLive = 300)
public record InterviewContext(
        /* Bot user id (e.g. Telegram id) — primary key in Redis. */
        @Id String userId,
        /* Current position within the question graph. */
        UUID currentNodeId,
        /* Diagnostic points aggregated per category: category → sum. */
        SortedMap<String, Integer> collectedPoints,
        /* Full answer log: nodeId → answer text. */
        Map<String, String> answers,
        /* Legal flag — whether the user consents to LLM process of their text. */
        boolean aiConsentGiven,
        /* Dynamic user profile extracted via node mapping keys. */
        Map<String, String> profileData
) {

    /** Compact constructor guarantees non-null collections, so callers never see NPEs. */
    public InterviewContext {
        collectedPoints = collectedPoints == null
                ? new TreeMap<>()
                : collectedPoints;
        answers = answers == null
                ? new HashMap<>()
                : answers;
        profileData = profileData == null
                ? new HashMap<>()
                : profileData;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Copy builder — handy when mutating a single field on an existing context. */
    public Builder toBuilder() {
        return new Builder()
                .userId(userId)
                .currentNodeId(currentNodeId)
                .collectedPoints(collectedPoints)
                .answers(answers)
                .aiConsentGiven(aiConsentGiven)
                .profileData(profileData);
    }

    public static final class Builder {
        private String userId;
        private UUID currentNodeId;
        private SortedMap<String, Integer> collectedPoints = new TreeMap<>();
        private Map<String, String> answers = new HashMap<>();
        private boolean aiConsentGiven;
        private Map<String, String> profileData = new HashMap<>();

        private Builder() {
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder currentNodeId(UUID currentNodeId) {
            this.currentNodeId = currentNodeId;
            return this;
        }

        public Builder collectedPoints(SortedMap<String, Integer> collectedPoints) {
            this.collectedPoints = collectedPoints == null
                    ? new TreeMap<>()
                    : new TreeMap<>(collectedPoints);
            return this;
        }

        public Builder answers(Map<String, String> answers) {
            this.answers = answers == null
                    ? new HashMap<>()
                    : new HashMap<>(answers);
            return this;
        }

        public Builder aiConsentGiven(boolean aiConsentGiven) {
            this.aiConsentGiven = aiConsentGiven;
            return this;
        }

        public Builder profileData(Map<String, String> profileData) {
            this.profileData = profileData == null
                    ? new HashMap<>()
                    : new HashMap<>(profileData);
            return this;
        }

        public InterviewContext build() {
            return new InterviewContext(
                    userId,
                    currentNodeId,
                    unmodifiableSortedMap(collectedPoints),
                    unmodifiableMap(answers),
                    aiConsentGiven,
                    unmodifiableMap(profileData)
            );
        }
    }
}
