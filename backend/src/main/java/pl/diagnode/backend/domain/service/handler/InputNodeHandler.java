package pl.diagnode.backend.domain.service.handler;

import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.service.handler.input.NodeInput;

public interface InputNodeHandler extends NodeHandler {

    InterviewContext handle(Node node, InterviewContext context, NodeInput input);

}
