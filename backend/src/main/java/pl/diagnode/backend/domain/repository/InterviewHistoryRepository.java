package pl.diagnode.backend.domain.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.diagnode.backend.domain.model.InterviewHistory;

public interface InterviewHistoryRepository extends JpaRepository<@NonNull InterviewHistory, @NonNull String> {

}
