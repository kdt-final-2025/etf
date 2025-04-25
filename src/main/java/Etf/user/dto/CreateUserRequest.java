package Etf.user.dto;

public record CreateUserRequest(
        String loginId,
        String password,
        String nickName,
        Boolean isLikePrivate) {
}
