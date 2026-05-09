package pl.diagnode.backend.infrastructure.ai;

import org.springframework.stereotype.Service;
import pl.diagnode.backend.domain.port.AiAnalysisClient;

import java.util.Map;

@Service
public class OpenAiClient implements AiAnalysisClient {

    @Override
    public Map<String, Integer> analyze(String userInput) {
        return Map.of();
    }

}
