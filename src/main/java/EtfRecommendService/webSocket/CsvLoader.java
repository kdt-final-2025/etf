package EtfRecommendService.webSocket;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvLoader {
    public static List<String> loadCodes(String csvPath) throws Exception {
        List<String> codes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            br.readLine(); // 헤더 건너뛰기
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                // 예: 첫 번째 컬럼에 종목코드가 있다면
                codes.add(cols[0].trim());
            }
        }
        return codes;
    }
}
