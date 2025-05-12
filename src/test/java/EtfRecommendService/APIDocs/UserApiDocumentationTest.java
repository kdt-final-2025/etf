package EtfRecommendService.APIDocs;

import EtfRecommendService.DatabaseCleanup;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.user.Password;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import EtfRecommendService.user.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@SpringBootTest
@Transactional
public class UserApiDocumentationTest {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    private String token;
    private Long userId;

    @BeforeEach
    public void setUp() {
        // DB 초기화
        databaseCleanup.execute();

        // 테스트용 유저 생성 및 토큰 발급
        Password pw = new Password("password");
        User saved = userRepository.save(
                new User("testuser", pw, "테스트사용자", false)
        );
        this.userId = saved.getId();
        this.token  = jwtProvider.createToken(saved.getLoginId());
    }

    @Test
    @DisplayName("회원 생성 API 문서화 테스트")
    void createUser_Success() throws Exception {
        CreateUserRequest req = new CreateUserRequest(
                "newuser",
                new Password("pass123"),
                "새닉네임",
                false
        );
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andDo(document("user-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").description("로그인 아이디"),
                                fieldWithPath("password.password").description("비밀번호"),
                                fieldWithPath("nickName").description("닉네임"),
                                fieldWithPath("isLikePrivate").description("댓글/좋아요 공개 여부")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("loginId").description("로그인 아이디"),
                                fieldWithPath("nickName").description("닉네임"),
                                fieldWithPath("isLikePrivate").description("댓글/좋아요 공개 여부")
                        )
                ));
    }

    @Test
    @DisplayName("회원 로그인 API 문서화 테스트")
    void loginUser_Success() throws Exception {
        UserLoginRequest loginReq = new UserLoginRequest("testuser", new Password("password"));
        String json = objectMapper.writeValueAsString(loginReq);

        mockMvc.perform(
                        post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andDo(document("user-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").description("로그인 아이디"),
                                fieldWithPath("password.password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("token").description("JWT 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("회원 프로필 수정 API 문서화 테스트")
    void updateProfile_Success() throws Exception {
        // given
        UserUpdateRequest updateReq = new UserUpdateRequest("새닉네임", false);
        String json = objectMapper.writeValueAsString(updateReq);

        // when + then
        mockMvc.perform(
                        patch("/api/v1/users")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andDo(document("user-update-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickName").description("변경할 닉네임"),
                                fieldWithPath("isLikePrivate").description("댓글/좋아요 공개 여부")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("nickName").description("닉네임"),
                                fieldWithPath("imageUrl").description("프로필 이미지 URL"),
                                fieldWithPath("isLikePrivate").description("댓글/좋아요 공개 여부")
                        )
                ));
    }


    @Test
    @DisplayName("회원 삭제 API 문서화 테스트")
    void deleteUser_Success() throws Exception {
        mockMvc.perform(
                        delete("/api/v1/users")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent())
                .andDo(document("user-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("비밀번호 변경 API 문서화 테스트")
    void updatePassword_Success() throws Exception {
        String newRaw = "newPassword123";
        UserPasswordRequest pwdReq = new UserPasswordRequest(
                new Password("password"),
                new Password(newRaw),
                new Password(newRaw)
        );
        String json = objectMapper.writeValueAsString(pwdReq);

        mockMvc.perform(
                        patch("/api/v1/users/me/password")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andDo(document("user-update-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("existingPassword.password").description("현재 비밀번호"),
                                fieldWithPath("newPassword.password").description("새 비밀번호"),
                                fieldWithPath("confirmNewPassword.password").description("비밀번호 확인")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 ID")
                        )
                ));
    }

    @Test
    @DisplayName("회원 상세 조회 API 문서화 테스트")
    void getUserDetail_Success() throws Exception {
        mockMvc.perform(
                        get("/api/v1/users/{userId}", userId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andDo(document("user-get-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("조회할 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("loginId").description("로그인 아이디"),
                                fieldWithPath("nickName").description("닉네임"),
                                fieldWithPath("imageUrl").description("프로필 이미지 URL"),
                                fieldWithPath("isLikePrivate").description("댓글/좋아요 공개 여부")
                        )
                ));
    }
}
