package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record UserLoginRequest(
        String loginId,
        String password,
        //Role: ADMIN, USER
        String role) {
}
