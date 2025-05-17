package EtfRecommendService.article;

public record ArticleResponse(
        Long id,
        String newsTitle,
        String newsLink,
        String imageUrl

) {
}
