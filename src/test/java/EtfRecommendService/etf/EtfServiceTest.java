package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.EtfResponse;
import EtfRecommendService.etf.dto.MonthlyEtfDto;
import EtfRecommendService.etf.dto.WeeklyEtfDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtfServiceTest {

    @Mock
    private EtfQueryRepository etfQueryRepository;

    @InjectMocks
    private EtfService etfService;

    private Pageable pageable;
    private Theme theme;
    private String keyword;
    private List<WeeklyEtfDto> weeklyEtfDtos;
    private List<MonthlyEtfDto> monthlyEtfDtos;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);
        theme = Theme.AI_DATA;
        keyword = "SAMSUNG";

        weeklyEtfDtos = Arrays.asList(
                new WeeklyEtfDto("Samsung Electronics ETF", "005930", Theme.AI_DATA, 1.5),
                new WeeklyEtfDto("Samsung Biologics ETF", "207940", Theme.AI_DATA, 2.3)
        );

        monthlyEtfDtos = Arrays.asList(
                new MonthlyEtfDto("Samsung Electronics ETF", "005930", Theme.AI_DATA, 5.2),
                new MonthlyEtfDto("Samsung Biologics ETF", "207940", Theme.AI_DATA, 7.8)
        );
    }

        @Test
        @DisplayName("기간이 'weekly'인 경우 주간 ETF 목록을 조회")
        void readAll_WithWeeklyPeriod_ReturnsWeeklyEtfResponse() {
            // Given
            String period = "weekly";
            long totalCount = 2L;

            when(etfQueryRepository.fetchTotalCount(theme, keyword)).thenReturn(totalCount);
            when(etfQueryRepository.findWeeklyEtfs(theme, keyword, pageable)).thenReturn(weeklyEtfDtos);

            // When
            EtfResponse<?> response = etfService.readAll(pageable, theme, keyword, period);

            // Then
            assertThat(response.totalCount()).isEqualTo(totalCount);
            assertThat(response.totalPage()).isEqualTo(1);
            assertThat(response.currentPage()).isEqualTo(1);
            assertThat(response.pageSize()).isEqualTo(20);
            assertThat(response.etfReadResponseList()).hasSize(2);
            assertThat(response.etfReadResponseList().get(0)).isInstanceOf(WeeklyEtfDto.class);

            verify(etfQueryRepository).fetchTotalCount(theme, keyword);
            verify(etfQueryRepository).findWeeklyEtfs(theme, keyword, pageable);
            verify(etfQueryRepository, never()).findMonthlyEtfs(any(), any(), any());
        }

        @Test
        @DisplayName("기간이 'monthly'인 경우 월간 ETF 목록을 조회")
        void readAll_WithMonthlyPeriod_ReturnsMonthlyEtfResponse() {
            // Given
            String period = "monthly";
            long totalCount = 2L;

            when(etfQueryRepository.fetchTotalCount(theme, keyword)).thenReturn(totalCount);
            when(etfQueryRepository.findMonthlyEtfs(theme, keyword, pageable)).thenReturn(monthlyEtfDtos);

            // When
            EtfResponse<?> response = etfService.readAll(pageable, theme, keyword, period);

            // Then
            assertThat(response.totalCount()).isEqualTo(totalCount);
            assertThat(response.totalPage()).isEqualTo(1);
            assertThat(response.currentPage()).isEqualTo(1);
            assertThat(response.pageSize()).isEqualTo(20);
            assertThat(response.etfReadResponseList()).hasSize(2);
            assertThat(response.etfReadResponseList().get(0)).isInstanceOf(MonthlyEtfDto.class);

            verify(etfQueryRepository).fetchTotalCount(theme, keyword);
            verify(etfQueryRepository).findMonthlyEtfs(theme, keyword, pageable);
            verify(etfQueryRepository, never()).findWeeklyEtfs(any(), any(), any());
        }

        @Test
        @DisplayName("검색 결과가 없는 경우 빈 목록을 반환")
        void readAll_WithNoResults_ReturnsEmptyList() {
            // Given
            String period = "weekly";
            long totalCount = 0L;

            when(etfQueryRepository.fetchTotalCount(theme, keyword)).thenReturn(totalCount);
            when(etfQueryRepository.findWeeklyEtfs(theme, keyword, pageable)).thenReturn(Collections.emptyList());

            // When
            EtfResponse<?> response = etfService.readAll(pageable, theme, keyword, period);

            // Then
            assertThat(response.totalCount()).isEqualTo(0L);
            assertThat(response.totalPage()).isEqualTo(0);
            assertThat(response.etfReadResponseList()).isEmpty();
        }

        @Test
        @DisplayName("페이징 잘 되는지 확인")
        void readAll_WithMultiplePages_CalculatesTotalPageCorrectly() {
            // Given
            String period = "weekly";
            long totalCount = 21L; // 20개씩 페이지네이션 시 2페이지 필요
            Pageable pageable = PageRequest.of(0, 20);

            when(etfQueryRepository.fetchTotalCount(theme, keyword)).thenReturn(totalCount);
            when(etfQueryRepository.findWeeklyEtfs(theme, keyword, pageable)).thenReturn(weeklyEtfDtos);

            // When
            EtfResponse<?> response = etfService.readAll(pageable, theme, keyword, period);

            // Then
            assertThat(response.totalCount()).isEqualTo(21L);
            assertThat(response.totalPage()).isEqualTo(2); // 총 2페이지
            assertThat(response.currentPage()).isEqualTo(1); // 현재 1페이지
        }
    }
