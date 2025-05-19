package EtfRecommendService.ai;


import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/survey")
public class SurveyController {
    private final SurveyService service;

    public SurveyController(SurveyService service) {
        this.service = service;
    }

    // 1) 질문 목록 조회
    @GetMapping("/questions")
    public List<Question> getQuestions() {
        return Questions.LIST;
    }

    // 2) 답변 10개 받고 AI로 추천
    @PostMapping("/recommend")
    public Mono<ResponseEntity<List<Recommendation>>> recommend(
            @RequestBody List<AnswerDto> answers
    ) {
        if (answers.size() != Questions.LIST.size()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return service.recommendWithAI(answers)
                .map(ResponseEntity::ok);
    }
}