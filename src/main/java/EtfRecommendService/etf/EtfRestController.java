package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.*;
import EtfRecommendService.loginUtils.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EtfRestController {

    private final EtfService etfService;

    //sortorder "" 변경 : Enum은 "" (빈 문자열)이 들어오면 바인딩 에러가 나기 때문, 없으면 null로 오게
    @GetMapping("/etfs")
    public ResponseEntity<EtfResponse> read(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) Theme theme,
                                           @RequestParam(required = false) SortOrder sortOrder) {
        Pageable pageable = PageRequest.of(page - 1, size);
        EtfResponse etfResponse = etfService.readAll(pageable, theme, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(etfResponse);
    }

    @GetMapping("/etfs/{etfId}")
    public ResponseEntity<EtfDetailResponse> findById(@PathVariable Long etfId){
        EtfDetailResponse etfDetailResponse = etfService.findById(etfId);
        return ResponseEntity.status(HttpStatus.OK).body(etfDetailResponse);
    }

    @PostMapping("/users/etfs/{etfId}/subscription")
    public ResponseEntity<SubscribeResponse> create(@LoginMember String memberLoginId, @PathVariable Long etfId){
        SubscribeResponse subscribeResponse = etfService.subscribe(memberLoginId, etfId);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscribeResponse);
    }

    @GetMapping("/users/etfs/subscribes")
    public ResponseEntity<SubscribeListResponse> subscribeReadAll(@LoginMember String memberLoginId,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "20") int size){
        Pageable pageable = PageRequest.of(page - 1, size);
        SubscribeListResponse subscribeListResponse = etfService.subscribeReadAll(pageable, memberLoginId);
        return ResponseEntity.status(HttpStatus.OK).body(subscribeListResponse);
    }

    @DeleteMapping("/users/etf/{etfId}/subscription")
    public ResponseEntity<SubscribeDeleteResponse> delete(@LoginMember String memberLoginId, @PathVariable Long etfId){
        SubscribeDeleteResponse subscribeDeleteResponse = etfService.unsubscribe(memberLoginId, etfId);
        return ResponseEntity.status(HttpStatus.OK).body(subscribeDeleteResponse);
    }


}
