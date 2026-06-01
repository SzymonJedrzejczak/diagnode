package pl.diagnode.backend.domain.service.handler.impl;

import pl.diagnode.backend.domain.exception.ValidationException;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.NodeOption;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.service.handler.InputNodeHandler;
import pl.diagnode.backend.domain.service.handler.input.ChoiceAnswer;
import pl.diagnode.backend.domain.service.handler.input.NodeInput;

import java.util.Collection;
import java.util.Map;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static pl.diagnode.backend.domain.model.enums.NodeType.SINGLE_ANSWER_QUESTION;

public class SingleAnswerQuestionHandler implements InputNodeHandler {

    @Override
    public InterviewContext handle(Node node, InterviewContext context, NodeInput input) {
        if (!(input instanceof ChoiceAnswer(String optionId))) {
            throw new IllegalStateException("Nieoczekiwany typ inputu: " + input.getClass().getSimpleName());
        }

        if (isBlank(optionId)) {
            throw new ValidationException("Odpowiedź nie może być pusta");
        }

        NodeOption selectedOption = node.getOptions().stream()
                .flatMap(Collection::stream)
                .filter(nodeOption -> optionId.equals(nodeOption.id()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nieznana opcja: " + optionId));

        InterviewContext updatedContext = node.getCategory()
                .map(category -> context.withCollectedPoints(Map.of(category.name(), selectedOption.score())))
                .orElse(context);

        return updatedContext.toBuilder().currentNodeId(selectedOption.nextNode().getId()).build();
    }

    @Override
    public NodeType getSupportedType() {
        return SINGLE_ANSWER_QUESTION;
    }

}
