package EtfRecommendService.ai;


import java.util.List;

public record Question(
        int id,
        String text,
        List<String> options
) {
}