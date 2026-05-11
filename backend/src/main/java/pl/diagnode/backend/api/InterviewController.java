package pl.diagnode.backend.api;

import io.netty.util.internal.StringUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.diagnode.backend.api.model.*;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.service.InterviewEngine;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/interview")
public class InterviewController {

    private final InterviewEngine interviewEngine;

    public InterviewController(InterviewEngine interviewEngine) {
        this.interviewEngine = interviewEngine;
    }

    @PostMapping("/{userId}/start")
    public ResponseEntity<InterviewResponse> start(@PathVariable String userId) {
        List<Node> nodes = interviewEngine.start(userId);
        return ResponseEntity.ok(toResponse(nodes));
    }

    @PostMapping("/{userId}/answer/open")
    public ResponseEntity<InterviewResponse> answerOpen(@PathVariable String userId,
                                                        @RequestBody AnswerRequest request) {
        List<Node> nodes = interviewEngine.answer(userId, request.userInput());
        return ResponseEntity.ok(toResponse(nodes));
    }

    @PostMapping("/{userId}/answer/choice")
    public ResponseEntity<InterviewResponse> answerChoice(@PathVariable String userId,
                                                          @RequestBody SingleChoiceAnswer request) {
        List<Node> nodes = interviewEngine.answer(userId, request.optionId());
        return ResponseEntity.ok(toResponse(nodes));
    }

    private InterviewResponse toResponse(List<Node> nodes) {
        List<NodeResponse> messages = nodes.stream()
                .map(node -> new NodeResponse(
                        node.getNodeType(),
                        node.getNodeContent(),
                        node.getMappingKey().orElse(StringUtil.EMPTY_STRING),
                        getOptions(node)))
                .toList();
        return new InterviewResponse(messages);
    }

    private static List<OptionResponse> getOptions(Node node) {
        return node.getOptions()
                .map(options -> options.stream()
                        .map(option -> new OptionResponse(option.id(), option.score()))
                        .toList())
                .orElse(Collections.emptyList());
    }
}
