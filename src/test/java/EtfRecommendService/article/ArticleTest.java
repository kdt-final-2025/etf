package EtfRecommendService.article;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ArticleTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testRead() {
        // Given
        Article article1 = new Article();
        ReflectionTestUtils.setField(article1, "title", "Title1");
        ReflectionTestUtils.setField(article1, "link", "Link1");
        ReflectionTestUtils.setField(article1, "imageUrl", "ImageUrl1");

        Article article2 = new Article();
        ReflectionTestUtils.setField(article2, "title", "Title2");
        ReflectionTestUtils.setField(article2, "link", "Link2");
        ReflectionTestUtils.setField(article2, "imageUrl", "ImageUrl2");

        when(articleRepository.findAll()).thenReturn(Arrays.asList(article1, article2));

        // When
        List<ArticleResponse> responses = articleService.read();

        // Then
        assertEquals(2, responses.size());
        assertEquals("Title1", responses.get(0).newsTitle());
        assertEquals("Link1", responses.get(0).newsLink());
        assertEquals("ImageUrl1", responses.get(0).imageUrl());
        assertEquals("Title2", responses.get(1).newsTitle());
        assertEquals("Link2", responses.get(1).newsLink());
        assertEquals("ImageUrl2", responses.get(1).imageUrl());

        verify(articleRepository, times(1)).findAll();
    }
}
