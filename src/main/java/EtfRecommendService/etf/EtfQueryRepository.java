package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.QEtf;
import EtfRecommendService.etf.domain.QEtfData;
import EtfRecommendService.etf.domain.QEtfReadData;
import EtfRecommendService.etf.dto.MonthlyEtfDto;
import EtfRecommendService.etf.dto.WeeklyEtfDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class EtfQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QEtf etf = QEtf.etf;
    private final QEtfData etfData = QEtfData.etfData;
    private final QEtfReadData etfReadData = QEtfReadData.etfReadData;

    public List<WeeklyEtfDto> findWeeklyEtfs(
            Theme theme,
            String keyword,
            Pageable pageable){
        return jpaQueryFactory
                .select(Projections.constructor(
                        WeeklyEtfDto.class,
                        etfReadData.etfName,
                        etfReadData.etfCode,
                        etfReadData.theme,
                        etfReadData.weeklyReturn))
                .from(etf)
                .where(themeEq(theme),
                        keywordContains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<MonthlyEtfDto> findMonthlyEtfs(Theme theme, String keyword, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        MonthlyEtfDto.class,
                        etfReadData.etfName,
                        etfReadData.etfCode,
                        etfReadData.theme,
                        etfReadData.monthlyReturn))
                .from(etf)
                .where(themeEq(theme), keywordContains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public Long fetchTotalCount(Theme theme, String keyword){
        Long count = jpaQueryFactory
                .select(etf.count())
                .from(etf)
                .where(themeEq(theme),
                        keywordContains(keyword))
                .fetchOne();

        //null 체크, 조회된거 없으면 0L로 처리
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
        return etf.etfName.containsIgnoreCase(keyword)  //대소문자 구분없이 검색
                .or(etf.etfCode.containsIgnoreCase(keyword));
    }
}


