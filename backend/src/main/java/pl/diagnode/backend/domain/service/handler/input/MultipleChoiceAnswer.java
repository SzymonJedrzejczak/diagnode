package pl.diagnode.backend.domain.service.handler.input;

import java.util.List;

public record MultipleChoiceAnswer(List<String> optionIds) implements NodeInput {
}
