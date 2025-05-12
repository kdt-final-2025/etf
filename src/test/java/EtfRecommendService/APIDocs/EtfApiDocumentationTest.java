package EtfRecommendService.APIDocs;

import EtfRecommendService.DatabaseCleanup;
import EtfRecommendService.etf.Theme;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.EtfProjection;
import EtfRecommendService.etf.dto.EtfReturnDto;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.user.Password;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@SpringBootTest
@Transactional
public class EtfApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private String token;
    private Long userId;

    private Pageable pageable;
    private Theme theme;
    private String keyword;
    private List<EtfReturnDto> weeklyEtfDtos;
    private List<EtfReturnDto> monthlyEtfDtos;

    private Long etfId1;
    private Long etfId2;

    @BeforeEach
    @Transactional
    public void setUp() {
        // DB 초기화
        databaseCleanup.execute();

        // 테스트용 유저 생성 및 토큰 발급
        Password pw = new Password("password");
        User saved = userRepository.save(
                new User("testuser", pw, "테스트사용자", false)
        );
        this.userId = saved.getId();
        this.token = jwtProvider.createToken(saved.getLoginId());

        loadTestData();          // etfName = "삼성전자 ETF", "SK하이닉스 ETF"
        this.keyword = "삼성전자";


        pageable = PageRequest.of(0, 20);
        theme = Theme.AI_DATA;


        weeklyEtfDtos = Arrays.asList(
                new EtfReturnDto("Samsung Electronics ETF", "005930", Theme.AI_DATA, 1.5),
                new EtfReturnDto("Samsung Biologics ETF", "207940", Theme.AI_DATA, 2.3)
        );

        monthlyEtfDtos = Arrays.asList(
                new EtfReturnDto("Samsung Electronics ETF", "005930", Theme.AI_DATA, 5.2),
                new EtfReturnDto("Samsung Biologics ETF", "207940", Theme.AI_DATA, 7.8)
        );

        loadTestData();

    }


    void loadTestData() {
        // 예: 테스트 시점의 정확한 타임스탬프
        LocalDateTime now = LocalDateTime.now();

        Etf e1 = Etf.builder()
                .etfName("삼성전자 ETF")              // etfName
                .etfCode("005930")                  // etfCode
                .companyName("Samsung Electronics") // companyName
                .listingDate(now)                   // LocalDateTime 타입
                .theme(Theme.AI_DATA)               // theme
                .build();

        Etf e2 = Etf.builder()
                .etfName("SK하이닉스 ETF")
                .etfCode("000660")
                .companyName("SK Hynix")
                .listingDate(now)
                .theme(Theme.AI_DATA)
                .build();


        // 2) Projection 엔티티 저장 (builder 없이 all-args 생성자 사용)
        //    첫 번째 파라미터(id)는 DB에서 자동 생성되므로 null로 둡니다.
        EtfProjection p1 = new EtfProjection(
                null,
                e1.getEtfName(),
                e1.getEtfCode(),
                e1.getTheme(),
                1.5,    // weeklyReturn
                5.2     // monthlyReturn
        );
        em.persist(p1);

        EtfProjection p2 = new EtfProjection(
                null,
                e2.getEtfName(),
                e2.getEtfCode(),
                e2.getTheme(),
                2.3,
                7.8
        );
        em.persist(p2);

        em.persist(e1);
        em.persist(e2);
        em.flush();

        this.etfId1 = e1.getId();
        this.etfId2 = e2.getId();

        em.clear();
    }


    @Test
    @DisplayName("ETF 목록 조회 API")
    void etfList() throws Exception {
        mockMvc.perform(get("/api/v1/etfs")
                        .param("theme", theme.name())
                        .param("keyword", "")
                        .param("page", String.valueOf(pageable.getPageNumber() + 1)) // 1‑base
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("etf-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        // ---- 1) 쿼리 파라미터 문서 ----
                        queryParameters(
                                parameterWithName("theme").description("ETF 테마"),
                                parameterWithName("keyword").description("검색 키워드").optional(),
                                parameterWithName("page").description("페이지 번호 (1‑base)"),
                                parameterWithName("size").description("페이지 크기")
                        ),

                        responseFields(
                                fieldWithPath("totalPage").description("총 페이지 수"),
                                fieldWithPath("totalCount").description("전체 건수"),
                                fieldWithPath("currentPage").description("현재 페이지"),
                                fieldWithPath("pageSize").description("페이지 크기"),

                                fieldWithPath("etfReadResponseList").description("ETF 목록 배열"),
                                fieldWithPath("etfReadResponseList[].etfName").description("ETF 이름"),
                                fieldWithPath("etfReadResponseList[].etfCode").description("ETF 코드"),
                                fieldWithPath("etfReadResponseList[].theme").description("ETF 테마"),
                                fieldWithPath("etfReadResponseList[].returnRate").description("수익률(%)")
                        )
                ));
    }

    @Test
    @DisplayName("ETF 상세 조회 API")
    void etfDetail() throws Exception {
        // 사전에 ETF 저장 로직 필요 (생략)
        Long etfId = 1L;

        this.mockMvc.perform(
                        get("/api/v1/etfs/{etfId}", etfId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("etfs-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("etfId").description("조회할 ETF ID")
                        ),
                        responseFields(
                                fieldWithPath("etfId").description("ETF ID"),
                                fieldWithPath("etfName").description("ETF 이름"),
                                fieldWithPath("etfCode").description("ETF 코드"),
                                fieldWithPath("companyName").description("운용사명"),
                                fieldWithPath("listingDate").description("상장일자")
                        )
                ));
    }

    @Test
    @DisplayName("구독 생성 API")
    void createSubscription() throws Exception {
        Long etfId = 1L;

        this.mockMvc.perform(
                        post("/api/v1/users/etfs/{etfId}/subscription", etfId)
                                .header("Authorization", "Bearer " + token)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andDo(document("subscription-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("etfId").description("구독할 ETF ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("구독 ID"),
                                fieldWithPath("etfId").description("구독한 ETF ID"),
                                fieldWithPath("createdAt").description("구독 생성일시"),
                                fieldWithPath("expiredAt").description("구독 만료일시")
                        )
                ));
    }

    @Test
    @DisplayName("구독 목록 조회 API")
    void listSubscriptions() throws Exception {

        Long etfId = 1L;

        mockMvc.perform(post("/api/v1/users/etfs/{etfId}/subscription", etfId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());



        this.mockMvc.perform(
                        get("/api/v1/users/etfs/subscribes")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "1")
                                .param("size", "20")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("subscription-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("totalPage").description("전체 페이지 수"),
                                fieldWithPath("totalCount").description("전체 결과 개수"),
                                fieldWithPath("currentPage").description("현재 페이지 번호"),
                                fieldWithPath("pageSize").description("페이지 크기"),
                                fieldWithPath("subscribeResponseList[].id").description("구독 ID"),
                                fieldWithPath("subscribeResponseList[].etfId").description("ETF ID"),
                                fieldWithPath("subscribeResponseList[].createdAt").description("구독 생성일시"),
                                fieldWithPath("subscribeResponseList[].expiredAt").description("구독 만료일시")
                        )
                ));
    }

    @Test
    @DisplayName("구독 취소 API")
    void deleteSubscription() throws Exception {
        Long etfId = 1L;

        mockMvc.perform(post("/api/v1/users/etfs/{etfId}/subscription", etfId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        this.mockMvc.perform(
                        delete("/api/v1/users/etf/{etfId}/subscription", etfId)
                                .header("Authorization", "Bearer " + token)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("subscription-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("etfId").description("취소할 구독 ETF ID")
                        ),
                        responseFields(
                                fieldWithPath("etfId").description("취소된 ETF ID")
                        )
                ));
    }
}
