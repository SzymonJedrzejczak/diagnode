package pl.diagnode.backend.domain.service.handler.impl;

import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.service.handler.ChoiceNodeHandler;

import static pl.diagnode.backend.domain.model.enums.NodeType.SINGLE_ANSWER_QUESTION;

public class SingleAnswerQuestionHandler implements ChoiceNodeHandler {

    @Override
    public InterviewContext handle(Node node, InterviewContext context, String optionId) {
        return null;
    }

    @Override
    public NodeType getSupportedType() {
        return SINGLE_ANSWER_QUESTION;
    }
}
