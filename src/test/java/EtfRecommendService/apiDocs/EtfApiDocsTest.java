package EtfRecommendService.apiDocs;


import EtfRecommendService.comment.controller.CommentRestController;
import EtfRecommendService.comment.dto.*;
import EtfRecommendService.comment.serviece.CommentService;
import EtfRecommendService.etf.EtfRestController;
import EtfRecommendService.etf.EtfService;
import EtfRecommendService.etf.Theme;
import EtfRecommendService.etf.dto.*;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.loginUtils.LoginMemberResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class EtfApiDocsTest {

    private MockMvc mockMvc;

    @Mock private EtfService etfService;
    @Mock private LoginMemberResolver loginMemberResolver;
    @Mock private JwtProvider jwtProvider;

    @InjectMocks private EtfRestController etfRestController;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private static String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDoc) throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(etfRestController)
                .setCustomArgumentResolvers(
                        loginMemberResolver,
                        new PageableHandlerMethodArgumentResolver()
                )
                .apply(documentationConfiguration(restDoc)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        // 로그인 유저 ID 주입
        given(loginMemberResolver.supportsParameter(any(MethodParameter.class)))
                .willAnswer(inv -> String.class.equals(inv.getArgument(0, MethodParameter.class).getParameterType()));

        given(loginMemberResolver.resolveArgument(
                any(), any(ModelAndViewContainer.class),
                any(NativeWebRequest.class), any(WebDataBinderFactory.class))
        ).willReturn("testUser");
    }


    @Test
    @DisplayName("1. ETF 목록 조회 API")
    void readEtfs() throws Exception {
        List<EtfReturnDto> content = List.of(
                new EtfReturnDto("Global Tech ETF", "GTECH", Theme.AI_DATA, 2.34)
        );
        EtfResponse response = new EtfResponse(1, 1L, 1, 20, content);
        given(etfService.readAll(any(Pageable.class), any(), anyString(), anyString()))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/etfs")
                        .param("page", "1")
                        .param("size", "20")
                        .param("theme", "AI_DATA")
                        .param("keyword", "Global")
                        .param("period", "weekly")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("etf-list",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (default = 1)"),
                                parameterWithName("size").description("페이지 크기 (default = 20)"),
                                parameterWithName("theme").description("ETF 테마 필터 (선택)"),
                                parameterWithName("keyword").description("검색 키워드 (선택)"),
                                parameterWithName("period").description("수익률 기간 (daily, weekly, monthly)")
                        ),
                        responseFields(
                                fieldWithPath("totalPage").description("총 페이지 수"),
                                fieldWithPath("totalCount").description("전체 ETF 수"),
                                fieldWithPath("currentPage").description("현재 페이지 번호"),
                                fieldWithPath("pageSize").description("페이지 크기"),
                                fieldWithPath("etfReadResponseList[].etfName").description("ETF 이름"),
                                fieldWithPath("etfReadResponseList[].etfCode").description("ETF 코드"),
                                fieldWithPath("etfReadResponseList[].theme").description("ETF 테마"),
                                fieldWithPath("etfReadResponseList[].returnRate").description("수익률")
                        )
                ));
    }

    @Test @DisplayName("2. ETF 상세 조회 API")
    void readEtfDetail() throws Exception {
        EtfDetailResponse detail = new EtfDetailResponse(
                1L, "Global Tech ETF", "GTECH", "TechCorp", LocalDateTime.of(2020,1,15,9,0)
        );
        given(etfService.findById(1L)).willReturn(detail);

        mockMvc.perform(get("/api/v1/etfs/{etfId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("etf-detail",
                        pathParameters(
                                parameterWithName("etfId").description("조회할 ETF ID")
                        ),
                        responseFields(
                                fieldWithPath("etfId").description("ETF ID"),
                                fieldWithPath("etfName").description("ETF 이름"),
                                fieldWithPath("etfCode").description("ETF 코드"),
                                fieldWithPath("companyName").description("운용사 이름"),
                                fieldWithPath("listingDate").description("상장 일시")
                        )
                ));
    }

    @Test @DisplayName("3. ETF 구독 생성 API")
    void createSubscription() throws Exception {
        SubscribeResponse resp = new SubscribeResponse(
                123L, 456L,
                LocalDateTime.of(2025,5,13,10,0),
                LocalDateTime.of(2026,5,13,10,0)
        );
        given(etfService.subscribe(eq("testUser"), eq(456L))).willReturn(resp);

        mockMvc.perform(post("/api/v1/users/etfs/{etfId}/subscription", 456L)
                        .header("Authorization", "Bearer dummy-token")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andDo(document("etf-subscription-create",
                        requestHeaders(
                                headerWithName("Authorization").description("사용자 JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("etfId").description("구독할 ETF ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("구독 ID"),
                                fieldWithPath("etfId").description("구독된 ETF ID"),
                                fieldWithPath("createdAt").description("구독 생성 일시"),
                                fieldWithPath("expiredAt").description("구독 만료 일시")
                        )
                ));
    }

    @Test @DisplayName("4. ETF 구독 목록 조회 API")
    void readSubscriptionList() throws Exception {
        List<SubscribeResponse> list = List.of(
                new SubscribeResponse(123L, 456L,
                        LocalDateTime.of(2025,5,13,10,0),
                        LocalDateTime.of(2026,5,13,10,0))
        );
        SubscribeListResponse resp = new SubscribeListResponse(1, 1L, 1, 20, list);
        given(etfService.subscribeReadAll(any(Pageable.class), eq("testUser"))).willReturn(resp);

        mockMvc.perform(get("/api/v1/users/etfs/subscribes")
                        .param("page", "1")
                        .param("size", "20")
                        .header("Authorization", "Bearer dummy-token")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("etf-subscription-list",
                        requestHeaders(
                                headerWithName("Authorization").description("사용자 JWT 토큰")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (default = 1)"),
                                parameterWithName("size").description("페이지 크기 (default = 20)")
                        ),
                        responseFields(
                                fieldWithPath("totalPage").description("총 페이지 수"),
                                fieldWithPath("totalCount").description("전체 구독 수"),
                                fieldWithPath("currentPage").description("현재 페이지 번호"),
                                fieldWithPath("pageSize").description("페이지 크기"),
                                fieldWithPath("subscribeResponseList[].id").description("구독 ID"),
                                fieldWithPath("subscribeResponseList[].etfId").description("ETF ID"),
                                fieldWithPath("subscribeResponseList[].createdAt").description("구독 생성 일시"),
                                fieldWithPath("subscribeResponseList[].expiredAt").description("구독 만료 일시")
                        )
                ));
    }

    @Test @DisplayName("5. ETF 구독 취소 API")
    void deleteSubscription() throws Exception {
        SubscribeDeleteResponse resp = new SubscribeDeleteResponse(456L);
        given(etfService.unsubscribe(eq("testUser"), eq(456L))).willReturn(resp);

        mockMvc.perform(delete("/api/v1/users/etf/{etfId}/subscription", 456L)
                        .header("Authorization", "Bearer dummy-token")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("etf-subscription-delete",
                        requestHeaders(
                                headerWithName("Authorization").description("사용자 JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("etfId").description("구독 취소할 ETF ID")
                        ),
                        responseFields(
                                fieldWithPath("etfId").description("구독이 취소된 ETF ID")
                        )
                ));
    }
}