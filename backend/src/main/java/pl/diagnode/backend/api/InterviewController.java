package pl.diagnode.backend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.diagnode.backend.domain.model.Node;
import pl.diagnode.backend.domain.service.InterviewEngine;

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
        List<NodeResponse> messages = nodes.stream()
                .map(n -> new NodeResponse(
                        n.getNodeType(),
                        n.getNodeContent(),
                        n.getMappingKey().orElse(null),
                        null))
                .toList();
        return ResponseEntity.ok(new InterviewResponse(messages));

    }

    @PostMapping("/{userId}/answer")
    public ResponseEntity<InterviewResponse> answer(@PathVariable String userId,
                                               @RequestBody AnswerRequest request) {
        List<Node> nodes = interviewEngine.answer(userId, request.userInput());
        List<NodeResponse> messages = nodes.stream()
                .map(n -> new NodeResponse(
                        n.getNodeType(),
                        n.getNodeContent(),
                        n.getMappingKey().orElse(null),
                        null))
                .toList();
        return ResponseEntity.ok(new InterviewResponse(messages));
    }

}
