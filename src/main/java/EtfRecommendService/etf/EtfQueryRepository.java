package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.QEtf;
import EtfRecommendService.etf.domain.QEtfProjection;
import EtfRecommendService.etf.dto.EtfReturnDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class EtfQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QEtfProjection etfProjection = QEtfProjection.etfProjection;
    private final QEtf etf = QEtf.etf;

    public List<EtfReturnDto> findEtfsByPeriod(
            Theme theme,
            String keyword,
            Pageable pageable,
            String period
    ) {
        NumberExpression<Double> returnRate =
                "monthly".equalsIgnoreCase(period) ? etfProjection.monthlyReturn : etfProjection.weeklyReturn;

        return jpaQueryFactory
                .select(Projections.constructor(
                        EtfReturnDto.class,
                        etf.etfName,
                        etf.etfCode,
                        etf.theme,
                        returnRate
                ))
                .from(etf)
                .leftJoin(etfProjection).on(etf.etfCode.eq(etfProjection.etfCode))
                .where(themeEq(theme), keywordContains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public Long fetchTotalCount(Theme theme, String keyword){
        Long count = jpaQueryFactory
                .select(etf.count())
                .from(etf)
                .leftJoin(etfProjection).on(etf.etfCode.eq(etfProjection.etfCode))
                .where(themeEq(theme),
                        keywordContains(keyword))
                .fetchOne();

        return count == null ? 0L : count;
    }

    private BooleanExpression themeEq(Theme theme) {
        if (theme == null) {
            return null;  // dsl은 null값 무시 -> 전체 조회
        }
        return etf.theme.eq(theme);
    }

    //검색어 기능 - 종목명, 종목코드
    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return etf.etfName.containsIgnoreCase(keyword)
                .or(etf.etfCode.containsIgnoreCase(keyword));
    }
}



