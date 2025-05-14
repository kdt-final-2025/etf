package EtfRecommendService.admin.dto;

import EtfRecommendService.user.Password;
import lombok.Builder;

@Builder
public record AdminLoginRequest(String loginId, String password, String roles) {
}


