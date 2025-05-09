package EtfRecommendService;


import EtfRecommendService.comment.dto.CommentCreateRequest;
import EtfRecommendService.etf.EtfRepository;
import EtfRecommendService.etf.Theme;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.user.Password;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;


import java.time.LocalDateTime;

import static io.restassured.path.json.JsonPath.given;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;



@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
public class ApiAutomationTest {


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    private EtfRepository etfRepository;

    @Autowired
    private UserRepository userRepository;

    private String token;

    // 이전 답변에서 설명드린 대로 클래스 필드로 선언
    private Etf etf1;
    private User user1; // User 객체도 필드로 선언하면 setUp 외부에서 접근 가능

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(MockMvcResultHandlers.print()) // 테스트 실행 후 요청과 응답의 상세 정보를 콘솔에 출력
                .apply(
                        documentationConfiguration(restDocumentation)
//                                .operationPreprocessors()
//                                .withRequestDefaults(Preprocessors.prettyPrint())  // 요청 JSON을 예쁘게
//                                .withResponseDefaults(Preprocessors.prettyPrint()) // 응답 JSON을 예쁘게
                )
                .build();


        // 1) 테스트용 Etf/User 저장
        Etf tempEtf = new Etf(
                "QQQ빼빼로ETF",
                "123456",
                "뺴뺴로잘만드는회사",
                LocalDateTime.parse("2025-03-07T15:20:00"),
                Theme.SECTOR
        );
        // 클래스 필드 etf1에 저장된 객체 할당
        this.etf1 = etfRepository.save(tempEtf);

        User tempUser = new User(
                "pepero",
                new Password("password"),
                "빼빼로부자",
                false
        );
        // 클래스 필드 user1에 저장된 객체 할당
        this.user1 = userRepository.save(tempUser);


        // 2) JWT 토큰 생성 (user1의 loginId 사용)
        token = jwtProvider.createToken(user1.getLoginId());
    }


    // 댓글 생성 테스트
    @DisplayName("댓글 생성 성공 테스트") // DisplayName 추가
    @Test
    void createComment_Success() throws Exception { // 메소드 이름 변경 (선택 사항)

        // 저장된 etf1 의 ID 를 사용
        CommentCreateRequest dto =

                new CommentCreateRequest(this.etf1.getId(),
                        "이 ETF에 대한 제 생각을 남깁니다! (수정됨)"); // 내용도 약간 변경

        String json = objectMapper.writeValueAsString(dto);


        this.mockMvc.perform(post("/api/v1/user/comments")
                        .header("Authorization", "Bearer " + token) // <--- RestAssured 예시에서 가져온 인증 헤더 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // RestAssured 예시의 HttpStatus.OK.value()와 동일
                .andDo(document("comment-create", // 문서 이름 유지
                        requestFields(
                                fieldWithPath("etfId").description("댓글을 달 ETF의 ID입니다"),
                                fieldWithPath("content").description("댓글 내용입니다")
                        )

                ));
    }

}