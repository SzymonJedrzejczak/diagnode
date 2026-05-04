package pl.diagnode.backend.domain.service.handler;

import org.springframework.stereotype.Service;
import pl.diagnode.backend.domain.exception.ValidationException;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.service.InputNodeHandler;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static pl.diagnode.backend.domain.model.enums.NodeType.OPEN_QUESTION;

@Service
public class OpenQuestionHandler implements InputNodeHandler {

    @Override
    public NodeType getSupportedType() {
        return OPEN_QUESTION;
    }

    @Override
    public InterviewContext handle(Node node, InterviewContext context, String userInput) {
        if (isBlank(userInput)) {
            throw new ValidationException("Odpowiedź nie może być pusta");
        }

        return context
                .withProfileEntry(node.getMappingKey(), userInput)
                .advanceToNextNode(node);
    }

}
