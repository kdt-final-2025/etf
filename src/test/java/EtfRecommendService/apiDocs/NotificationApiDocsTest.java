package EtfRecommendService.apiDocs;

import EtfRecommendService.notification.NotificationRestController;
import EtfRecommendService.notification.NotificationService;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class NotificationApiDocsTest {
    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationRestController notificationController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDoc) {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(notificationController)
                .apply(documentationConfiguration(restDoc))
                .build();
    }

    @Test
    @DisplayName("SSE 알림 스트림 연결 API")
    void streamNotifications() throws Exception {
        // stub: 어떤 receiverId, receiverType에도 SseEmitter 반환
        SseEmitter emitter = new SseEmitter();
        doReturn(emitter)
                .when(notificationService)
                .createEmitter(anyLong(), any());

        mockMvc.perform(get("/sse/notifications")
                        .param("receiverId", "1")
                        .param("receiverType", "USER")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andDo(document("notification-stream",
                        queryParameters(
                                parameterWithName("receiverId").description("알림을 받을 사용자 ID"),
                                parameterWithName("receiverType").description("알림 수신자 타입 (USER 또는 ADMIN)")
                        )
                        // SSE 특성상 body 스니펫은 생략
                ));
    }
}
