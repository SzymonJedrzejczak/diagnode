package pl.diagnode.backend.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.diagnode.backend.domain.mapper.InterviewMapper;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.repository.InterviewContextCache;
import pl.diagnode.backend.domain.repository.InterviewHistoryRepository;
import pl.diagnode.backend.domain.repository.NodeRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrates a single step of the interview:
 * <ol>
 *     <li>loads the current session from Redis (fast path) or Postgres (fallback),</li>
 *     <li>dispatches to the {@link NodeHandler} matching the current node type,</li>
 *     <li>persists the resulting context back to Redis (and to Postgres if it was restored from there),</li>
 *     <li>returns the next node to present to the user.</li>
 * </ol>
 */
@Service
public class InterviewEngine {

    private final Map<NodeType, NodeHandler> handlersByType;
    private final InterviewContextCache interviewContextCache;
    private final InterviewHistoryRepository interviewHistoryRepository;
    private final NodeRepository nodeRepository;
    private final InterviewMapper interviewMapper;
    private final String startNodeKey;

    public InterviewEngine(List<NodeHandler> handlers,
                           InterviewContextCache interviewContextCache,
                           NodeRepository nodeRepository,
                           InterviewHistoryRepository interviewHistoryRepository,
                           InterviewMapper interviewMapper,
                           @Value("${interview.start-node-key}") String startNodeKey) {
        this.interviewContextCache = interviewContextCache;
        this.nodeRepository = nodeRepository;
        this.interviewHistoryRepository = interviewHistoryRepository;
        this.interviewMapper = interviewMapper;
        this.startNodeKey = startNodeKey;
        this.handlersByType = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(NodeHandler::getSupportedType, h -> h));
    }

    /**
     * Processes a single user input and returns the next node to present.
     */
    public List<Node> start(String userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        InterviewContext context = findContext(userId)
                .orElseGet(() -> createNewContext(userId));
        return advanceAutomaticNodes(context);
    }

    public List<Node> answer(String userId, String userInput) {
        Objects.requireNonNull(userId, "userId must not be null");

        InterviewContext context = findContext(userId)
                .orElseThrow(() -> new IllegalStateException("No active session for user: " + userId));

        Node currentNode = loadNode(context.currentNodeId());

        NodeHandler handler = resolveHandler(currentNode.getNodeType());

        if (!(handler instanceof InputNodeHandler inputHandler)) {
            throw new IllegalStateException("Current node does not accept input: " + currentNode.getNodeType());
        }

        InterviewContext updated = inputHandler.handle(currentNode, context, userInput);

        persist(updated);
        return advanceAutomaticNodes(updated);
    }

    private List<Node> advanceAutomaticNodes(InterviewContext context) {
        List<Node> nodesToSend = new ArrayList<>();
        Node node = loadNode(context.currentNodeId());

        while (resolveHandler(node.getNodeType()) instanceof AutomaticNodeHandler automaticHandler) {
            nodesToSend.add(node);
            context = automaticHandler.handle(node, context);
            persist(context);
            node = loadNode(context.currentNodeId());
        }

        nodesToSend.add(node);
        return nodesToSend;
    }

    private Optional<InterviewContext> findContext(String userId) {
        return getContextFromCache(userId)
                .or(() -> getContextFromHistory(userId));
    }

    private Optional<InterviewContext> getContextFromCache(String userId) {
        return interviewContextCache.findById(userId);
    }

    private Optional<InterviewContext> getContextFromHistory(String userId) {
        return interviewHistoryRepository.findById(userId)
                .map(interviewMapper::toContext);
    }

    private InterviewContext createNewContext(String userId) {
        Node startingNode = nodeRepository.findByNodeKey(startNodeKey)
                .orElseThrow(() -> new IllegalStateException("Missing starting node: " + startNodeKey));

        return InterviewContext.builder()
                .userId(userId)
                .currentNodeId(startingNode.getId())
                .build();
    }

    private Node loadNode(java.util.UUID nodeId) {
        return nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalStateException("Missing node: " + nodeId));
    }

    private NodeHandler resolveHandler(NodeType type) {
        NodeHandler handler = handlersByType.get(type);
        if (handler == null) {
            throw new IllegalStateException("No handler registered for node type: " + type);
        }
        return handler;
    }

    /**
     * Always refreshes Redis; additionally persists to Postgres when the context
     * was restored from the long-term store (so the two stays in sync).
     */
    private void persist(InterviewContext context) {
        interviewContextCache.save(context);
        interviewHistoryRepository.save(interviewMapper.toHistory(context));
    }
}
