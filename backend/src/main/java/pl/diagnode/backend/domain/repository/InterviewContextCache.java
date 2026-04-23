package pl.diagnode.backend.domain.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.CrudRepository;
import pl.diagnode.backend.domain.model.InterviewContext;

public interface InterviewContextCache extends CrudRepository<@NonNull InterviewContext, @NonNull String> {


}
