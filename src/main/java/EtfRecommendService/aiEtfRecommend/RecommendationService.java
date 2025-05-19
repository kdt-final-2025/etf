package EtfRecommendService.aiEtfRecommend;



import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RecommendationService {
    private final GeminiClient geminiClient;

    public RecommendationService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public List<Recommendation> recommend(QuestionnaireRequest req) {
        try {
            return geminiClient.fetchRecommendations(req);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("추천 생성 실패: " + e.getMessage(), e);
        }
    }
}
