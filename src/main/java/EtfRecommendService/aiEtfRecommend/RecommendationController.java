package EtfRecommendService.aiEtfRecommend;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {
    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<List<Recommendation>> recommend(@RequestBody QuestionnaireRequest req) {
        List<Recommendation> recs = service.recommend(req);
        return ResponseEntity.ok(recs);
    }
}