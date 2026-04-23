package pl.diagnode.backend.domain.model;

/**
 * Internal transfer object that carries an {@link InterviewContext} together with
 * a flag indicating whether it was loaded from the Redis cache ({@code true})
 * or reconstructed from the Postgres history ({@code false} — it must be re-cached).
 */
public record ContextDTO(InterviewContext context, boolean fromCache) {
}
