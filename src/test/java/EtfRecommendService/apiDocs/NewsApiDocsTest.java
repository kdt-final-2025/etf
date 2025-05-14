package EtfRecommendService.apiDocs;

import EtfRecommendService.news.NewsController;
import EtfRecommendService.news.NewsResponse;
import EtfRecommendService.news.NewsService;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class NewsApiDocsTest {

    private MockMvc mockMvc;

    @Mock
    private NewsService newsService;              // ★ @Mock 사용

    @InjectMocks
    private NewsController newsController;        // ★ @InjectMocks 사용

    private List<NewsResponse> sampleNews;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDoc) {
        // StandaloneSetup 으로 MockMvc 초기화
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(newsController)
                .apply(documentationConfiguration(restDoc))
                .build();

        sampleNews = List.of(
                new NewsResponse("제목1", "https://link1", "https://img1.jpg"),
                new NewsResponse("제목2", "https://link2", "https://img2.jpg")
        );
    }

    @Test
    @DisplayName("뉴스 목록 조회 API 문서화")
    void readNews() throws Exception {
        // given
        given(newsService.read()).willReturn(sampleNews);

        // when / then
        mockMvc.perform(get("/api/v1/news")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("news-read",
                        responseFields(
                                fieldWithPath("[]").type(ARRAY).description("뉴스 목록"),
                                fieldWithPath("[].newsTitle").type(STRING).description("뉴스 제목"),
                                fieldWithPath("[].newsLink").type(STRING).description("뉴스 링크"),
                                fieldWithPath("[].imageUrl").type(STRING).description("썸네일 이미지 URL")
                        )
                ));
    }
}
