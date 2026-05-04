package pl.diagnode.backend.domain.service;

import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;

public interface AutomaticNodeHandler extends NodeHandler {

    InterviewContext handle(Node node, InterviewContext context);

}
