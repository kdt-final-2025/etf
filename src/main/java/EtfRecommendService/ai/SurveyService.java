package EtfRecommendService.ai;

import java.util.List;

import org.springframework.stereotype.Service;


import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class SurveyService {
    public SurveyService(AiService aiService) {
        this.aiService = aiService;
    }

    private final  AiService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Mono<List<Recommendation>> recommendWithAI(List<AnswerDto> answers) {
        // 1) 질문·답변 텍스트 합치기
        StringBuilder qa = new StringBuilder();
        for (AnswerDto a : answers) {
            Question q = Questions.LIST.get(a.questionId() - 1);
            qa.append(q.text())
                    .append("\n➜ 답변: ")
                    .append(a.answer())
                    .append("\n\n");
        }

        // 2) AI용 프롬프트 생성
        String prompt = """
                아래는 투자 성향을 파악하기 위한 10개 질문과 사용자의 답변입니다.
                이 정보를 기반으로 한국 주식 시장에서 3~5개의 종목을 추천하고,
                각 종목에 대한 이유를 JSON 배열 형태로 반환해 주세요.
                
                %s
                """.formatted(qa.toString());

        // 3) Gemini API 호출 → JSON 파싱
        return aiService.ask("", prompt)
                .map(this::parseRecommendationsFromJson);
    }

    private List<Recommendation> parseRecommendationsFromJson(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<Recommendation>>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("추천 결과 파싱 실패", e);
        }
    }
}
