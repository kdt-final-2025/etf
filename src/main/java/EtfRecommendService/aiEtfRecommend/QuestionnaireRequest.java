package EtfRecommendService.aiEtfRecommend;

public record QuestionnaireRequest(
        String goal,              // 1. 왜 투자하려고 하나요? (예: "노후 대비해서 모으고 싶어요")
        String duration,          // 2. 얼마나 오래 넣어둘 계획인가요? (예: "6개월 ~ 1년")
        String amount,            // 3. 얼마를 투자할 수 있나요? (예: "3천만 ~ 5천만 원")
        String feeling,           // 4. 가격이 오르내릴 때 기분은? (예: "보통이에요")
        String experience,        // 5. 투자해 본 경험이 있나요? (예: "전혀 없어요")
        String expectedReturn,    // 6. 연 수익을 어느 정도 기대하나요? (예: "3~5%")
        String etfType,           // 7. 어떤 종류 ETF를 원하나요? (예: "주식+채권 섞인 ETF")
        String region,            // 8. 어느 나라나 분야에 관심 있나요? (예: "IT·헬스케어 ETF")
        String esgInterest,       // 9. 착한 투자 관심도는? (예: "꽤 관심 있어요")
        String tradingFrequency   // 10. 언제든 사고팔고 싶은가요? 아니면 오래 두고 싶은가요? (예: "일 년에 한 번 이하")
) {}
