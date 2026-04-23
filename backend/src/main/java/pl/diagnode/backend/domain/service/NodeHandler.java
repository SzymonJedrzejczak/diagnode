package pl.diagnode.backend.domain.service;

import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.Node;
import pl.diagnode.backend.domain.model.enums.NodeType;

/**
 * Strategy for processing a single node of a given {@link NodeType}.
 * Implementations are auto-discovered by {@link InterviewEngine} via Spring.
 */
public interface NodeHandler {

    /** The node type this handler is responsible for. */
    NodeType getSupportedType();

    /**
     * Processes the user's input against the current node and returns the updated
     * session context (points, answers, profile data, pointer to the next node).
     *
     * @param node      current node being processed
     * @param context   current immutable session snapshot
     * @param userInput raw user-provided text (may be {@code null} for system-driven nodes)
     * @return new context reflecting the outcome of handling this node
     */
    InterviewContext handleNode(Node node, InterviewContext context, String userInput);
}
