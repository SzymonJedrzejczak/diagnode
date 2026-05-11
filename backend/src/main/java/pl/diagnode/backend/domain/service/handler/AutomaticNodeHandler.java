package pl.diagnode.backend.domain.service.handler;

import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;

public interface AutomaticNodeHandler extends NodeHandler {

    InterviewContext handle(Node node, InterviewContext context);

}
