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
import EtfRecommendService.reply.controller.ReplyController;
import EtfRecommendService.reply.dto.RepliesPageList;
import EtfRecommendService.reply.dto.ReplyRequest;
import EtfRecommendService.reply.dto.ReplyResponse;
import EtfRecommendService.reply.service.ReplyService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class ReplyApiDocsTest {

    private MockMvc mockMvc;

    @Mock
    private ReplyService replyService;

    @InjectMocks
    private ReplyController replyController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDoc) {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(replyController)
                .apply(documentationConfiguration(restDoc))
                .build();
    }

    @Test
    @DisplayName("대댓글 생성 API")
    void createReply() throws Exception {
        doNothing().when(replyService).create(anyString(), any(ReplyRequest.class));

        mockMvc.perform(post("/api/v1/user/replies")
                        .header("Authorization", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "commentId": 1,
                                  "content": "답글 내용"
                                }
                                """))
                .andExpect(status().isCreated())
                .andDo(document("reply-create",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("commentId").type(NUMBER).description("댓글 ID"),
                                fieldWithPath("content").type(STRING).description("답글 내용")
                        )
                ));
    }

    @Test
    @DisplayName("댓글에 대한 대댓글 목록 조회 API")
    void readAllReplies() throws Exception {
        long commentId = 1L;
        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        ReplyResponse rr = ReplyResponse.builder()
                .id(100L)
                .userId(10L)
                .nickName("nick1")
                .content("내용")
                .likesCount(3)
                .createdAt(LocalDateTime.now())
                .build();
        RepliesPageList resp = RepliesPageList.builder()
                .page(page)
                .size(size)
                .totalElements(1L)
                .totalPages(1)
                .commentId(commentId)
                .replyResponses(List.of(rr))
                .build();

        doReturn(resp).when(replyService).readAll(any(), anyLong(), any(Pageable.class));

        mockMvc.perform(get("/api/v1/user/replies/{commentId}", commentId)
                        .header("Authorization", "user1")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("reply-read-all",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("page").type(NUMBER).description("현재 페이지"),
                                fieldWithPath("size").type(NUMBER).description("페이지 크기"),
                                fieldWithPath("totalElements").type(NUMBER).description("전체 대댓글 수"),
                                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수"),
                                fieldWithPath("commentId").type(NUMBER).description("댓글 ID"),
                                fieldWithPath("replyResponses").type(ARRAY).description("대댓글 목록"),
                                fieldWithPath("replyResponses[].id").type(NUMBER).description("대댓글 ID"),
                                fieldWithPath("replyResponses[].userId").type(NUMBER).description("작성자 ID"),
                                fieldWithPath("replyResponses[].nickName").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("replyResponses[].content").type(STRING).description("대댓글 내용"),
                                fieldWithPath("replyResponses[].likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("replyResponses[].createdAt").type(ARRAY).description("작성일시")
                        )
                ));
    }

    @Test
    @DisplayName("대댓글 수정 API")
    void updateReply() throws Exception {
        long replyId = 100L;
        doNothing().when(replyService).update(anyString(), anyLong(), any(ReplyRequest.class));

        mockMvc.perform(put("/api/v1/user/replies/{replyId}", replyId)
                        .header("Authorization", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "commentId": 1,
                                  "content": "수정된 내용"
                                }
                                """))
                .andExpect(status().isNoContent())
                .andDo(document("reply-update",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        pathParameters(
                                parameterWithName("replyId").description("대댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("commentId").type(NUMBER).description("댓글 ID"),
                                fieldWithPath("content").type(STRING).description("수정할 내용")
                        )
                ));
    }

    @Test
    @DisplayName("대댓글 삭제 API")
    void deleteReply() throws Exception {
        long replyId = 100L;
        doNothing().when(replyService).delete(anyString(), anyLong());

        mockMvc.perform(delete("/api/v1/user/replies/{replyId}", replyId)
                        .header("Authorization", "user1"))
                .andExpect(status().isNoContent())
                .andDo(document("reply-delete",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        pathParameters(
                                parameterWithName("replyId").description("대댓글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("대댓글 좋아요 토글 API")
    void toggleLike() throws Exception {
        long replyId = 100L;
        doNothing().when(replyService).toggleLike(anyString(), anyLong());

        mockMvc.perform(post("/api/v1/user/replies/{replyId}/likes", replyId)
                        .header("Authorization", "user1"))
                .andExpect(status().isOk())
                .andDo(document("reply-toggle-like",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        pathParameters(
                                parameterWithName("replyId").description("대댓글 ID")
                        )
                ));
    }
}