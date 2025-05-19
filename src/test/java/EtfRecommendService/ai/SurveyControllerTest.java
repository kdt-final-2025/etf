package EtfRecommendService.ai;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SurveyControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        // HTTPS 로 로컬 테스트 서버에 요청
        RestAssured.baseURI = "https://localhost";
        RestAssured.port    = port;
        RestAssured.useRelaxedHTTPSValidation(); // self-signed 인증서 허용
    }

    @DisplayName("실제 AI 응답 및 상태 코드 확인 테스트 (HTTPS)")
    @Test
    void 실제AI응답확인테스트() {
        List<AnswerDto> answers = List.of(
                new AnswerDto(1, "가"), new AnswerDto(2, "나"),
                new AnswerDto(3, "다"), new AnswerDto(4, "라"),
                new AnswerDto(5, "마"), new AnswerDto(6, "가"),
                new AnswerDto(7, "나"), new AnswerDto(8, "다"),
                new AnswerDto(9, "라"), new AnswerDto(10, "마")
        );

        Response resp = given().log().all()
                .relaxedHTTPSValidation()        // 이 줄을 추가해도 좋습니다
                .contentType("application/json")
                .body(answers)
                .when()
                .post("/api/v1/survey/recommend")
                .andReturn();

        System.out.println("===== TEST LOG =====");
        System.out.println("Status Code: " + resp.getStatusCode());
        System.out.println("Response Body:");
        System.out.println(resp.asPrettyString());
        System.out.println("====================");
    }
}
