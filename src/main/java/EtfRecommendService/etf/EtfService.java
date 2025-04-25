package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EtfService {

    private final EtfRepository etfRepository;

    public EtfService(EtfRepository etfRepository) {
        this.etfRepository = etfRepository;
    }

    public EtfResponse readAll(Pageable pageable, Theme theme, SortOrder sortOrder) {
        return null;
    }

    public EtfDetailResponse findById(Long etfId) {
        return null;
    }

    public SubscribeResponse save(String memberLoginId, Long etfId) {
        return null;
    }

    public SubscribeListResponse subscribeReadAll(Pageable pageable, String memberLoginId) {
        return null;
    }

    public SubscribeDeleteResponse delete(String memberLoginId, Long etfId) {
        return null;
    }
}
