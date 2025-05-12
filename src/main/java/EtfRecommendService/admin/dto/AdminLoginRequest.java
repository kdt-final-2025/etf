package EtfRecommendService.admin.dto;

import EtfRecommendService.user.Password;

public record AdminLoginRequest(String loginId, String password, String roles) {
}


