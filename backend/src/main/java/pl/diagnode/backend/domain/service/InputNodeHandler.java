package pl.diagnode.backend.domain.service;

import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.Node;

public interface InputNodeHandler extends NodeHandler {

    InterviewContext handle(Node node, InterviewContext context, String userInput);

}
