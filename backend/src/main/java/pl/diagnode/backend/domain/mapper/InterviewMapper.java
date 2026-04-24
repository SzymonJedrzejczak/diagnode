package pl.diagnode.backend.domain.mapper;

import org.mapstruct.Mapper;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.InterviewHistory;

/**
 * Two-way mapping between the transient Redis {@link InterviewContext}
 * and the persistent {@link InterviewHistory} entity.
 */

@Mapper(componentModel = "spring")
public interface InterviewMapper {

    InterviewContext toContext(InterviewHistory history);

    InterviewHistory toHistory(InterviewContext context);
}
