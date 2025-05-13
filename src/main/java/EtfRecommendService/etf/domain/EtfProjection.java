package EtfRecommendService.etf.domain;

import EtfRecommendService.etf.Theme;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class EtfProjection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String etfName;

    private String etfCode;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    private double weeklyReturn;

    private double monthlyReturn;
}
