package Etf.user;

import Etf.loginUtils.JwtProvider;
import Etf.loginUtils.SecurityUtils;
import Etf.user.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public User getByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId).orElseThrow(
                () -> new NoSuchElementException("회원을 찾을 수 없습니다."));
    }

    public UserResponse create(CreateUserRequest userRequest) {

        Password password = new Password(userRequest.password());

        User user = new User(
                userRequest.loginId(),
                password,
                userRequest.nickName(),
                userRequest.isLikePrivate());

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                userRequest.loginId(),
                userRequest.nickName(),
                userRequest.isLikePrivate());
    }

    public UserLoginResponse login(UserLoginRequest loginRequest) {
        User user = getByLoginId(loginRequest.loginId());

        user.getPassword().equalsPassword(loginRequest.password());

        return new UserLoginResponse(jwtProvider.createToken(loginRequest.loginId()));
    }

    @Transactional
    public UserUpdateResponse profileUpdate(String loginId, UserUpdateRequest updateRequest) {
        User user = getByLoginId(loginId);

        user.profileUpdate(
                updateRequest.nickName(),
                updateRequest.isLikePrivate());

        return new UserUpdateResponse(
                user.getId(),
                user.getNickName(),
                user.getImageUrl(),
                user.getIsLikePrivate());
    }

    @Transactional
    public UserDeleteResponse delete(String loginId) {
        User user = getByLoginId(loginId);

        return new UserDeleteResponse(user.getId(), user.getIsDeleted());
    }

    @Transactional
    public UserPasswordResponse passwordUpdate(String loginId, UserPasswordRequest passwordRequest) {
        User user = getByLoginId(loginId);

        Password password = new Password(passwordRequest.password());

        password.PasswordUpdateError(user.getPassword());

        user.passwordUpdate(passwordRequest.password());

        userRepository.save(user);

        return new UserPasswordResponse(user.getId());
    }

    public MypageResponse findByUser(String loginId, Long userId) {
        getByLoginId(loginId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchElementException("존재하지 않는 유저, id : " + userId));

        return new MypageResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickName(),
                user.getImageUrl(),
                user.getIsLikePrivate());
    }


}
