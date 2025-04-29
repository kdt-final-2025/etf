package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.QEtf;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EtfQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QEtf qEtf = QEtf.etf;

    public EtfQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    //조회(null이면 전체, 테마 필터링, 정렬)
    public Page<Etf> findAllByThemeAndSort(Theme theme, SortOrder sortOrder, Pageable pageable){
        //데이터 가져오기
        List<Etf> content = jpaQueryFactory
                .selectFrom(qEtf)
                .where(themeEq(theme))
                .orderBy(getOrderSpecifier(sortOrder))
                .orderBy(getOrderSpecifier(sortOrder))
                .offset(pageable.getOffset())  //페이지네이션
                .limit(pageable.getPageSize())  //페이지네이션
                .fetch();

        // count 조회 커리 - total 쿼리는 limit적용하면 안됨 (offset, limit 없이 개수만 세기 때문)
        Long total = jpaQueryFactory
                .select(qEtf.count())
                .from(qEtf)
                .where(themeEq(theme))
                .fetchOne();

        //Page 객체로 포장해서 반환 , null방지
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression themeEq(Theme theme) {
        if (theme == null) {
            return null;  // theme 조건 없이 전체 조회
        }
        return qEtf.theme.eq(theme);
    }

    //검색어 기능 - 종목명, 종목코드
    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return qEtf.etfName.containsIgnoreCase(keyword)
                .or(qEtf.etfCode.containsIgnoreCase(keyword));
    }

    private OrderSpecifier<?> getOrderSpecifier(SortOrder sortOrder) {
        if (sortOrder == null){
            sortOrder = SortOrder.VOLUME; // 기본 정렬 = 거래량
        }
        switch (sortOrder) {
            case VOLUME:
                return qEtf.volume.desc();
            case RISING_RATE:
                return qEtf.risingRate.desc();
            case FALLING_RATE:
                return qEtf.fallingRate.asc();
            default:
                return qEtf.volume.desc(); // 기본 정렬은 거래량
        }
    }
}


