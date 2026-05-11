package pl.diagnode.backend.domain.service.handler.impl;

import org.springframework.stereotype.Service;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.service.handler.AutomaticNodeHandler;

import static pl.diagnode.backend.domain.model.enums.NodeType.INTERLUDE;

@Service
public class InterludeHandler implements AutomaticNodeHandler {

    @Override
    public NodeType getSupportedType() {
        return INTERLUDE;
    }

    @Override
    public InterviewContext handle(Node node, InterviewContext context) {
        return context.advanceToNextNode(node);
    }
}
