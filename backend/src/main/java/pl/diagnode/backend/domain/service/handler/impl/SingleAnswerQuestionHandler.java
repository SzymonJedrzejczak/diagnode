package pl.diagnode.backend.domain.service.handler.impl;

import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.service.handler.InputNodeHandler;
import pl.diagnode.backend.domain.service.handler.input.NodeInput;

import static pl.diagnode.backend.domain.model.enums.NodeType.SINGLE_ANSWER_QUESTION;

public class SingleAnswerQuestionHandler implements InputNodeHandler {

    @Override
    public InterviewContext handle(Node node, InterviewContext context, NodeInput input) {
        return null;
    }

    @Override
    public NodeType getSupportedType() {
        return SINGLE_ANSWER_QUESTION;
    }

}
