package EtfRecommendService.apiDocs;

import EtfRecommendService.user.Password;
import EtfRecommendService.user.UserController;
import EtfRecommendService.user.UserService;
import EtfRecommendService.user.dto.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;

import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class UserApiDocsTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDoc) {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .apply(documentationConfiguration(restDoc))
                .build();
    }

    @Test
    @DisplayName("회원 가입 API")
    void createUser() throws Exception {
        // 1) Password 객체 생성
        Password password = new Password("pass123");

        // 2) 요청 DTO
        CreateUserRequest req = new CreateUserRequest(
                "user1",
                password,
                "nick",
                true
        );

        // 3) 서비스가 반환할 응답 목
        UserResponse resp = new UserResponse(
                1L,
                "user1",
                "nick",
                true
        );
        // 4) create(...) 호출을 stub
        given(userService.create(any(CreateUserRequest.class)))
                .willReturn(resp);
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "user1",
                                  "password": "pass123",
                                  "nickName": "nick",
                                  "isLikePrivate": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andDo(document("user-create",
                        requestFields(
                                fieldWithPath("loginId").type(STRING).description("로그인 ID"),
                                fieldWithPath("password").type(STRING).description("비밀번호"),
                                fieldWithPath("nickName").type(STRING).description("닉네임"),
                                fieldWithPath("isLikePrivate").type(BOOLEAN).description("좋아요 정보 비공개 여부")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("loginId").type(STRING).description("로그인 ID"),
                                fieldWithPath("nickName").type(STRING).description("닉네임"),
                                fieldWithPath("isLikePrivate").type(BOOLEAN).description("좋아요 정보 비공개 여부")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 API")
    void login() throws Exception {

        Password password = new Password("pass123");
        UserLoginRequest req = new UserLoginRequest("user1", password);
        UserLoginResponse resp = new UserLoginResponse("token-xyz");
        given(userService.login(any(UserLoginRequest.class))).willReturn(resp);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "user1",
                                  "password": "pass123"
                                }
                                """))
                .andExpect(status().isOk())
                .andDo(document("user-login",
                        requestFields(
                                fieldWithPath("loginId").type(STRING).description("로그인 ID"),
                                fieldWithPath("password").type(STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("token").type(STRING).description("인증 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 업데이트 API")
    void updateProfile() throws Exception {
        // (1) 요청 파라미터
        UserUpdateRequest req = new UserUpdateRequest("newNick", false);

        // (2) 서비스가 반환할 DTO: id, nickName, imageUrl, isLikePrivate 순
        UserUpdateResponse resp = new UserUpdateResponse(
                1L,
                "newNick",
                "https://example.com/profile.jpg",  // imageUrl
                false
        );

        // (3) stub: any() 매처로 첫 번째 인자(null 혹은 token)도 허용
        doReturn(resp)
                .when(userService)
                .UpdateProfile(any(), any(UserUpdateRequest.class));

        // (4) 테스트 수행 및 문서화
        mockMvc.perform(patch("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickName": "newNick",
                                  "isLikePrivate": false
                                }
                                """))
                .andExpect(status().isOk())
                .andDo(document("user-update",
                        requestFields(
                                fieldWithPath("nickName").type(STRING).description("변경할 닉네임"),
                                fieldWithPath("isLikePrivate").type(BOOLEAN).description("좋아요 정보 비공개 여부")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("nickName").type(STRING).description("닉네임"),
                                fieldWithPath("imageUrl").type(STRING).description("변경된 프로필 이미지 URL"),
                                fieldWithPath("isLikePrivate").type(BOOLEAN).description("좋아요 정보 비공개 여부")
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴 API")
    void deleteUser() throws Exception {
        String auth = "user1";
        mockMvc.perform(delete("/api/v1/users")
                        .header("Authorization", auth))
                .andExpect(status().isNoContent())
                .andDo(document("user-delete",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 변경 API")
    void updatePassword() throws Exception {
        // 준비
        Password existing = new Password("old1");
        Password updated = new Password("new1");
        Password confirm = new Password("new1");
        UserPasswordRequest req = new UserPasswordRequest(existing, updated, confirm);

        // ★ Stub 수정: doReturn + any() 매처
        UserPasswordResponse resp = new UserPasswordResponse(42L);
        doReturn(resp)
                .when(userService)
                .updatePassword(any(), any(UserPasswordRequest.class));

        // 실행 & 문서화
        mockMvc.perform(patch("/api/v1/users/me/password")
                        .header("Authorization", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "old1",
                                  "newPassword": "new1",
                                  "confirmNewPassword": "new1"
                                }
                                """))
                .andExpect(status().isOk())
                .andDo(document("user-password",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("oldPassword").type(STRING).description("현재 비밀번호"),
                                fieldWithPath("newPassword").type(STRING).description("새 비밀번호"),
                                fieldWithPath("confirmNewPassword").type(STRING).description("새 비밀번호 확인")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("변경된 회원 ID")
                        )
                ));
    }

    @Test
    @DisplayName("내 댓글/댓댓글 조회 API")
    void getUserComments() throws Exception {
        String auth = "user1";
        long userId = 2L;
        int page = 1;
        int size = 10;

        // 실제 반환할 DTO: page, size, totalElements, totalPages, commentsAndReplies
        long totalElements = 0L;
        long totalPages = 0L;
        List<getUserCommentsAndReplies> list = List.of();
        UserPageResponse resp = new UserPageResponse(page, size, totalElements, totalPages, list);

        // Stub: any() 매처로 auth, userId, Pageable 모두 허용
        doReturn(resp)
                .when(userService)
                .findUserComments(any(), anyLong(), any(Pageable.class));

        mockMvc.perform(get("/api/v1/users/comment/{userId}", userId)
                        .header("Authorization", auth)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(document("user-comments",
                        pathParameters(
                                parameterWithName("userId").description("조회할 회원 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (1~)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("page").type(NUMBER).description("요청한 페이지 번호"),
                                fieldWithPath("size").type(NUMBER).description("요청한 페이지 크기"),
                                fieldWithPath("totalElements").type(NUMBER).description("전체 댓글·댓댓글 수"),
                                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수"),
                                fieldWithPath("commentsAndReplies").type(ARRAY).description("댓글/댓댓글 목록")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 API")
    void updateImage() throws Exception {
        // (1) Stub에 사용할 auth, file, resp 준비
        String auth = "user1";
        MockMultipartFile file = new MockMultipartFile(
                "images", "profile.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3}
        );
        long userId = 42L;
        UserProfileResponse resp = new UserProfileResponse(
                userId,
                "https://s3/bucket/profile.jpg"
        );

        // (2) Stub: any() 매처로 header(null 또는 "user1"), MultipartFile(any) 허용
        doReturn(resp)
                .when(userService)
                .imageUpdate(any(), any(MultipartFile.class));

        // (3) PATCH multipart 요청 빌드
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/users/image")
                        .file(file)
                        .header("Authorization", auth)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                )
                .andExpect(status().isOk())
                .andDo(document("user-image",
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                        ),
                        requestParts(
                                partWithName("images").description("업로드할 프로필 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("imageUrl").type(STRING).description("저장된 이미지 URL")
                        )
                ));
    }


    @Test
    @DisplayName("회원 상세 조회 API")
    void getUserDetail() throws Exception {
        String auth = "user1";
        long userId = 3L;

        // (1) 실제 반환할 DTO
        UserDetailResponse resp = new UserDetailResponse(
                3L,
                "user3",
                "nick3",
                "https://example.com/profile.jpg",
                true
        );

        // (2) Stub: any() 매처로 auth(null or "user1"), anyLong()도 허용
        doReturn(resp)
                .when(userService)
                .findByUserId(any(), anyLong());

        // (3) 테스트 수행
        mockMvc.perform(get("/api/v1/users/{userId}", userId)
                        // 헤더 없이 호출하면 auth == null 이고, 매처 any() 가 이를 허용
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("user-detail",
                        pathParameters(
                                parameterWithName("userId").description("조회할 회원 ID")
                        ),
                        // 이 테스트는 헤더가 없으므로 requestHeaders 생략 가능
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("loginId").type(STRING).description("로그인 ID"),
                                fieldWithPath("nickName").type(STRING).description("닉네임"),
                                fieldWithPath("imageUrl").type(STRING).description("프로필 이미지 URL"),
                                fieldWithPath("isLikePrivate").type(BOOLEAN).description("좋아요 정보 비공개 여부")
                        )
                ));
    }

}

