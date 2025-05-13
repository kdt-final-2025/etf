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
import EtfRecommendService.report.controller.ReportController;
import EtfRecommendService.report.dto.CommentReportResponse;
import EtfRecommendService.report.dto.ReplyReportResponse;
import EtfRecommendService.report.dto.ReportListResponse;
import EtfRecommendService.report.dto.ReportRequest;
import EtfRecommendService.report.service.ReportService;
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

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


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
public class ReportApiDocsTest {


    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDoc) {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(reportController)
                .apply(documentationConfiguration(restDoc))
                .build();
    }

    @Test
    @DisplayName("신고 생성 API")
    void createReport() throws Exception {
        // stub reporting behavior
        doNothing().when(reportService).create(anyString(), any(ReportRequest.class));

        mockMvc.perform(post("/api/v1/user/reports")
                        .header("Authorization", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "commentId": 1,
                                  "replyId": null,
                                  "reportReason": "INAPPROPRIATE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andDo(document("report-create",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("commentId").type(NUMBER).description("신고할 댓글 ID (없으면 null)"),
                                fieldWithPath("replyId").type(NUMBER).optional().description("신고할 대댓글 ID (없으면 null)"),
                                fieldWithPath("reportReason").type(STRING).description("신고 사유")
                        )
                ));
    }

    @Test
    @DisplayName("전체 신고 목록 조회 API")
    void readAllReports() throws Exception {
        // Prepare sample responses
        CommentReportResponse cr = CommentReportResponse.builder()
                .reportId(1L)
                .reportReason("SPAM")
                .createdAt(LocalDateTime.now())
                .build();
        ReplyReportResponse rr = ReplyReportResponse.builder()
                .reportId(2L)
                .reportReason("ABUSE")
                .createdAt(LocalDateTime.now())
                .build();
        ReportListResponse resp = ReportListResponse.builder()
                .commentReportResponseList(List.of(cr))
                .replyReportResponseList(List.of(rr))
                .build();

        // Stub for any authorization header
        doReturn(resp).when(reportService).readAll(any());

        mockMvc.perform(get("/api/v1/admin/reports")
                        .header("Authorization", "admin1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("report-read-all",
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰")
                        ),
                        responseFields(
                                fieldWithPath("commentReportResponseList").type(ARRAY).description("댓글 신고 목록"),
                                fieldWithPath("commentReportResponseList[].reportId").type(NUMBER).description("댓글 신고 ID"),
                                fieldWithPath("commentReportResponseList[].reportReason").type(STRING).description("댓글 신고 사유"),
                                fieldWithPath("commentReportResponseList[].createdAt").type(ARRAY).description("댓글 신고 생성일시"),
                                fieldWithPath("replyReportResponseList").type(ARRAY).description("대댓글 신고 목록"),
                                fieldWithPath("replyReportResponseList[].reportId").type(NUMBER).description("대댓글 신고 ID"),
                                fieldWithPath("replyReportResponseList[].reportReason").type(STRING).description("대댓글 신고 사유"),
                                fieldWithPath("replyReportResponseList[].createdAt").type(ARRAY).description("대댓글 신고 생성일시")
                        )
                ));
    }
}

