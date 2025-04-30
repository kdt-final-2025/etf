package EtfRecommendService.user;

import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.user.dto.*;
import EtfRecommendService.user.exception.PasswordMismatchException;
import EtfRecommendService.user.exception.UserMismatchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static EtfRecommendService.user.User.userMismatchExceptionMessage;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserQueryRepository userQueryRepository;

    public User getByLoginId(String loginId) {
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId).orElseThrow(
                () -> new UserMismatchException(userMismatchExceptionMessage));
    }

    public UserResponse create(CreateUserRequest userRequest) {

        User user = new User(
                userRequest.loginId(),
                userRequest.password(),
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

        if (!loginRequest.password().isSamePassword(user.getPassword())) {
            throw new UserMismatchException(userMismatchExceptionMessage);
        }

        return new UserLoginResponse(jwtProvider.createToken(loginRequest.loginId()));
    }

    @Transactional
    public UserUpdateResponse UpdateProfile(String loginId, UserUpdateRequest updateRequest) {
        User user = getByLoginId(loginId);

        user.updateProfile(
                updateRequest.nickName(),
                updateRequest.isLikePrivate());

        return new UserUpdateResponse(
                user.getId(),
                user.getNickName(),
                user.getImageUrl(),
                user.getIsLikePrivate());
    }

    @Transactional
    public void delete(String loginId) {
        User user = getByLoginId(loginId);

        user.deleteUser();
    }

    @Transactional
    public UserPasswordResponse updatePassword(String loginId, UserPasswordRequest passwordRequest) {
        User user = getByLoginId(loginId);

        if (!passwordRequest.newPassword().isSamePassword(passwordRequest.confirmNewPassword())) {
            throw new PasswordMismatchException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 유저의 비밀번호와 입력받은 비밀번호가 같지않으면 예외처리
        if (!user.isSamePassword(passwordRequest.existingPassword())) {
            throw new PasswordMismatchException("유저의 비밀번호와 입력받은 비밀번호가 같지 않습니다.");
        }

        // 유저의 비밀번호와 입력받은 새 비밀번호가 같으면 예외처리
        if (user.isSamePassword(passwordRequest.confirmNewPassword())) {
            throw new RuntimeException("변경할 비밀번호가 같습니다.");
        }

        user.updatePassword(passwordRequest.newPassword());

        return new UserPasswordResponse(user.getId());
    }

    public UserPageResponse findByUser(String loginId, Long userId, Pageable pageable) {
        getByLoginId(loginId);

        userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원입니다."));

        List<UserCommentResponse> list = userQueryRepository.findUserComment(userId, pageable);

        long totalCount = userQueryRepository.countUserComments(userId);

        return new UserPageResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                totalCount,
                (totalCount + pageable.getPageSize() - 1) / pageable.getPageSize(),
                list);
    }


}
