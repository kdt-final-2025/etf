package Etf.user.dto;

import Etf.user.Password;

public record UserCreateRequest(
        String loginId,
        Password password,
        String nickName,
        Boolean isLikePrivate) {
}
