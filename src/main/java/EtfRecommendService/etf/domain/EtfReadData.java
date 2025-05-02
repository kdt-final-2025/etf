package EtfRecommendService.etf.domain;

import EtfRecommendService.etf.Theme;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EtfReadData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String etfName;

    private String etfCode;

    private Theme theme;

    private double weeklyReturn;

    private double monthlyReturn;
}
