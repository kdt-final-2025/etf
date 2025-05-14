package EtfRecommendService.apiDocs;

import EtfRecommendService.admin.AdminController;
import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.admin.dto.AdminLoginResponse;
import EtfRecommendService.admin.AdminService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class AdminApiDocsTest {
    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDoc) {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(adminController)
                .apply(documentationConfiguration(restDoc))
                .build();
    }

    @Test
    @DisplayName("관리자 로그인 API")
    void login() throws Exception {
        // stub
        AdminLoginResponse resp = new AdminLoginResponse("token-xyz");
        doReturn(resp).when(adminService).login(any(AdminLoginRequest.class));

        // perform
        mockMvc.perform(post("/api/v1/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "admin1",
                                  "password": "adminpass"
                                }
                                """))
                .andExpect(status().isOk())
                .andDo(document("admin-login",
                        requestFields(
                                fieldWithPath("loginId").type(STRING).description("관리자 로그인 ID"),
                                fieldWithPath("password").type(STRING).description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("token").type(STRING).description("인증 토큰")
                        )
                ));
    }
}