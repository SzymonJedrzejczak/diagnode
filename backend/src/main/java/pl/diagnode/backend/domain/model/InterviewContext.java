package pl.diagnode.backend.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.*;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSortedMap;
import static java.util.Optional.ofNullable;
import static org.apache.logging.log4j.util.Strings.isBlank;

/**
 * Short-lived interview session kept in Redis. Immutable by design — every state
 * transition produces a new instance via {@link Builder}.
 * <p>
 * Collections exposed via accessors are always unmodifiable views, regardless of
 * how the record was constructed (builder, deserialization, direct constructor call).
 */
@RedisHash(value = "interview_sessions", timeToLive = 300)
public record InterviewContext(@Id String userId,
                               UUID currentNodeId,
                               SortedMap<String, Integer> collectedPoints,
                               Map<String, String> answers,
                               boolean aiConsentGiven,
                               Map<String, String> profileData) {

    /**
     * Compact constructor — the single place that guarantees two invariants:
     * collections are never {@code null}, and they are always exposed as
     * unmodifiable views (defensive copies are taken to decouple the record
     * from the caller's references).
     */
    public InterviewContext {
        collectedPoints = unmodifiableSortedMap(collectedPoints == null
                ? new TreeMap<>()
                : new TreeMap<>(collectedPoints));
        answers = unmodifiableMap(answers == null
                ? new HashMap<>()
                : new HashMap<>(answers));
        profileData = unmodifiableMap(profileData == null
                ? new HashMap<>()
                : new HashMap<>(profileData));
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Copy builder — handy when mutating a single field on an existing context.
     */
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
                    collectedPoints,
                    answers,
                    aiConsentGiven,
                    profileData
            );
        }

    }

    public InterviewContext advanceToNextNode(Node node) {
        return ofNullable(node.getNextNode())
                .map(Node::getId)
                .map(nextId -> toBuilder().currentNodeId(nextId).build())
                .orElseThrow(() -> new IllegalStateException("Node '%s' has no next node".formatted(node.getId())));
    }

    public InterviewContext withProfileEntry(Optional<String> key, String value) {
        return key.map(k -> withProfileEntry(k, value))
                .orElse(this);
    }

    public InterviewContext withProfileEntry(String key, String value) {
        if (isBlank(key)) {
            return this;
        }
        Map<String, String> updated = new HashMap<>(profileData);
        updated.put(key, value);
        return toBuilder().profileData(updated).build();
    }
}
