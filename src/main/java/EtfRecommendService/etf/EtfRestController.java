package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.*;
import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.webSocket.CsvLoader;
import EtfRecommendService.webSocket.WebSocketConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EtfRestController {

    private final EtfService etfService;
    private final WebSocketConnectionService webSocketConnectionService;
    private final CsvLoader csvLoader;

    @GetMapping("/etfs")
    public ResponseEntity<EtfResponse> read(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(required = false) Theme theme,
                                            @RequestParam(required = false, defaultValue = "") String keyword,
                                            @RequestParam(defaultValue = "weekly") String period) {
        Pageable pageable = PageRequest.of(page - 1, size);
        EtfResponse etfResponse = etfService.readAll(pageable, theme,keyword, period);
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

    //웹소켓
    //어떤 종목코드를 구독할지
    @RestController
    public class StockCodeController {
        @GetMapping("/api/stocks")
        public List<String> getCodes(@RequestParam int page, @RequestParam int size) throws Exception{
            var all = csvLoader.loadCodes("src/main/resources/etf_data_result.csv");
            return all.subList(page*size, Math.min(all.size(), (page+1)*size));
        }
    }


    //상세보기


}
