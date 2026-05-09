package pl.diagnode.backend.domain.port;

import java.util.Map;

public interface AiAnalysisClient {

    Map<String, Integer> analyze(String userInput);

}
