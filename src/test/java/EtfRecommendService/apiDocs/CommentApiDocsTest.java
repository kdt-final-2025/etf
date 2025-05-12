package EtfRecommendService.apiDocs;

import EtfRecommendService.comment.controller.CommentRestController;
import EtfRecommendService.comment.dto.CommentCreateRequest;
import EtfRecommendService.comment.dto.CommentResponse;
import EtfRecommendService.comment.dto.CommentUpdateRequest;
import EtfRecommendService.comment.dto.CommentsPageList;
import EtfRecommendService.comment.dto.ToggleLikeResponse;
import EtfRecommendService.comment.serviece.CommentService;
import EtfRecommendService.loginUtils.LoginMemberResolver;
import EtfRecommendService.loginUtils.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class CommentApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private LoginMemberResolver loginMemberResolver;

    @MockBean
    private JwtProvider jwtProvider;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // LoginMemberResolver should only support String parameters (i.e., @LoginMember)
        given(loginMemberResolver.supportsParameter(any(MethodParameter.class)))
                .willAnswer(invocation -> {
                    MethodParameter param = invocation.getArgument(0);
                    return String.class.equals(param.getParameterType());
                });

        // Return adminUserId when resolving the @LoginMember-annotated parameter
        given(loginMemberResolver.resolveArgument(
                any(MethodParameter.class),
                any(ModelAndViewContainer.class),
                any(NativeWebRequest.class),
                any(WebDataBinderFactory.class)
        )).willReturn("adminUserId");

        // Stub service layer for admin comment read
        CommentResponse sample = CommentResponse.builder()
                .id(1L)
                .userId(42L)
                .nickName("tester")
                .content("관리자용 신고 댓글 조회 테스트입니다.")
                .likesCount(3L)
                .createdAt(LocalDateTime.of(2025, 5, 12, 21, 0))
                .build();
        given(commentService.readOneComment("adminUserId", 1L))
                .willReturn(sample);
    }

    @Test
    @DisplayName("댓글 생성 API")
    void createComment() throws Exception {
        doNothing().when(commentService).create(anyString(), any(CommentCreateRequest.class));
        CommentCreateRequest request = new CommentCreateRequest(1L, "좋은 ETF 댓글");

        mockMvc.perform(post("/api/v1/user/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andDo(document("comment-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("etfId").description("댓글을 달 ETF ID"),
                                fieldWithPath("content").description("댓글 내용")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 목록 조회 API")
    void readComments() throws Exception {
        CommentResponse sample = CommentResponse.builder()
                .id(1L)
                .userId(42L)
                .nickName("userA")
                .content("샘플 댓글")
                .likesCount(3L)
                .createdAt(LocalDateTime.now())
                .build();
        CommentsPageList pageList = CommentsPageList.builder()
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .etfId(100L)
                .commentResponses(List.of(sample))
                .build();
        given(commentService.readAll(any(Pageable.class), eq(100L))).willReturn(pageList);

        mockMvc.perform(get("/api/v1/user/comments")
                        .param("etf_id", "100")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,DESC")
                        .header("Authorization", "Bearer token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("comments-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("etf_id").description("조회할 ETF ID"),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 방식 (예: createdAt,DESC)")
                        ),
                        responseFields(
                                fieldWithPath("page").description("현재 페이지"),
                                fieldWithPath("size").description("페이지 크기"),
                                fieldWithPath("totalElements").description("총 댓글 수"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("etfId").description("ETF ID"),
                                fieldWithPath("commentResponses[].id").description("댓글 ID"),
                                fieldWithPath("commentResponses[].userId").description("작성자 유저 ID"),
                                fieldWithPath("commentResponses[].nickName").description("작성자 닉네임"),
                                fieldWithPath("commentResponses[].content").description("댓글 내용"),
                                fieldWithPath("commentResponses[].likesCount").description("좋아요 수"),
                                fieldWithPath("commentResponses[].createdAt").description("작성 일시")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 수정 API")
    void updateComment() throws Exception {
        doNothing().when(commentService).update(anyString(), eq(1L), any(CommentUpdateRequest.class));
        CommentUpdateRequest req = new CommentUpdateRequest("수정된 댓글 내용");

        mockMvc.perform(put("/api/v1/user/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(asJsonString(req)))
                .andExpect(status().isOk())
                .andDo(document("comment-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("수정할 댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("수정할 댓글 내용")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제 API")
    void deleteComment() throws Exception {
        doNothing().when(commentService).delete(anyString(), eq(1L));

        mockMvc.perform(delete("/api/v1/user/comments/{commentId}", 1L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andDo(document("comment-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("삭제할 댓글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 좋아요 토글 API")
    void toggleLike() throws Exception {
        ToggleLikeResponse toggle = new ToggleLikeResponse(1L, true, 5L);
        given(commentService.toggleLike(anyString(), eq(1L))).willReturn(toggle);

        mockMvc.perform(post("/api/v1/user/comments/{commentId}/likes", 1L)
                        .header("Authorization", "Bearer token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("comment-toggle-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("좋아요 토글할 댓글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("관리자 신고된 댓글 단건 조회")
    void readOneComment_Admin() throws Exception {
        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .get("/api/v1/admin/comments/{commentId}", 1L)
                                .header("Authorization", "Bearer {관리자 JWT 토큰}")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("admin-comment-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("조회 대상 댓글의 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("댓글 고유 ID"),
                                fieldWithPath("userId").description("댓글 작성자 사용자 ID"),
                                fieldWithPath("nickName").description("댓글 작성자 닉네임"),
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("likesCount").description("좋아요 수"),
                                fieldWithPath("createdAt").description("댓글 작성 일시 (ISO 8601)")
                        )
                ));
    }
}