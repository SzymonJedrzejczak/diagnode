package pl.diagnode.backend.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.diagnode.backend.domain.mapper.InterviewMapper;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.Node;
import pl.diagnode.backend.domain.model.enums.NodeType;
import pl.diagnode.backend.domain.repository.InterviewContextCache;
import pl.diagnode.backend.domain.repository.InterviewHistoryRepository;
import pl.diagnode.backend.domain.repository.NodeRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Orchestrates a single step of the interview:
 * <ol>
 *     <li>loads the current session from Redis (fast path) or Postgres (fallback), </li>
 *     <li>dispatches to the {@link NodeHandler} matching the current node type,</li>
 *     <li>persists the resulting context back to Redis and to Postgres</li>
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
    public Node processStep(String userId, String userInput) {
        Objects.requireNonNull(userId, "userId must not be null");

        // 1. Znajdujemy lub tworzymy punkt startowy
        InterviewContext session = findContext(userId)
                .orElseGet(() -> createNewContext(userId));

        // 2. Pobieramy definicję pytania i odpowiedni "silnik" (handler)
        Node currentNode = loadNode(session.currentNodeId());
        NodeHandler handler = resolveHandler(currentNode.getNodeType());

        // 3. Logika biznesowa: transformacja starego stanu w nowy
        InterviewContext updated = handler.handleNode(currentNode, session, userInput);

        // 4. Zabezpieczenie danych (Write-Through do Redis i Postgres)
        persist(updated);

        // Zwracamy węzeł, na którym użytkownik wylądował po tej operacji
        return loadNode(updated.currentNodeId());
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
