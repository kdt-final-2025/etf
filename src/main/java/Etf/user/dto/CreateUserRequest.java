package Etf.user.dto;

import Etf.user.Password;

public record CreateUserRequest(
        String loginId,
        String password,
        String nickName,
        Boolean isLikePrivate) {
}
