package pl.diagnode.backend.domain.service.handler;

import org.springframework.stereotype.Service;
import pl.diagnode.backend.domain.exception.ValidationException;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.port.AiAnalysisClient;
import pl.diagnode.backend.domain.service.InputNodeHandler;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static pl.diagnode.backend.domain.model.enums.NodeType.OPEN_QUESTION;

@Service
public class OpenQuestionHandler implements InputNodeHandler {

    private final AiAnalysisClient aiAnalysisClient;

    public OpenQuestionHandler(AiAnalysisClient aiAnalysisClient) {
        this.aiAnalysisClient = aiAnalysisClient;
    }

    @Override
    public NodeType getSupportedType() {
        return OPEN_QUESTION;
    }

    @Override
    public InterviewContext handle(Node node, InterviewContext context, String userInput) {
        if (isBlank(userInput)) {
            throw new ValidationException("Odpowiedź nie może być pusta");
        }

        InterviewContext updatedContext = node.getCategory().isEmpty()
                ? context.withProfileEntry(node.getMappingKey(), userInput)
                : processAnswer(node, context, userInput);

        return updatedContext.advanceToNextNode(node);
    }

    private InterviewContext processAnswer(Node node, InterviewContext context, String userInput) {
        InterviewContext baseContext = context.withAnswers(node.getId(), userInput);

        if (!context.aiConsentGiven()) {
            return baseContext;
        }

        return baseContext.withCollectedPoints(aiAnalysisClient.analyze(userInput));
    }
}
