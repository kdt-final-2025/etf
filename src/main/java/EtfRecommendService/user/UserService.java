package EtfRecommendService.user;

import EtfRecommendService.S3Service;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.loginUtils.JwtTokens;
import EtfRecommendService.security.RefreshTokenDetails;
import EtfRecommendService.security.RefreshTokenRepository;
import EtfRecommendService.security.TokenNotFoundException;
import EtfRecommendService.user.dto.*;
import EtfRecommendService.user.exception.UserMismatchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static EtfRecommendService.user.exception.ErrorMessages.USER_MISMATCH;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final S3Service s3Service;
    private final UserQueryRepository userQueryRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public User getByLoginId(String loginId) {
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId).orElseThrow(
                () -> new UserMismatchException(USER_MISMATCH));
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
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.loginId(), loginRequest.password());

        Authentication authentication = authenticationManager.authenticate(token);

        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        //{accessToken, refreshToken}
        String [] tokens = generateTokens(userDetail);

        return new UserLoginResponse(tokens[0],tokens[1]);
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
                user.isLikePrivate());
    }

    @Transactional
    public void delete(String loginId) {
        User user = getByLoginId(loginId);

        user.deleteUser();
    }

    @Transactional
    public UserPasswordResponse updatePassword(String loginId, UserPasswordRequest passwordRequest) {
        User user = getByLoginId(loginId);

        user.updatePassword(
                passwordRequest.existingPassword(),
                passwordRequest.newPassword());

        return new UserPasswordResponse(user.getId());
    }

    public UserPageResponse findUserComments(String loginId, Long userId, Pageable pageable) {
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원입니다."));

        User loginUser = getByLoginId(loginId);

        boolean selfProfile = loginUser.isSelfProfile(userId);

        // 만약 찾는유저의 정보가 비공개 설정이고 로그인한 유저의 조회가 아니라면
        if (findUser.isLikePrivate() && !selfProfile) {
            return new UserPageResponse(
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize(),
                    0,
                    0,
                    null
            );
        }

        List<getUserCommentsAndReplies> getUserCommentRespons = userQueryRepository.getUserCommentsAndReplies(userId, pageable);

        long totalCount = userQueryRepository.totalCount(userId);

        return new UserPageResponse(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                totalCount,
                (totalCount + pageable.getPageSize() - 1) / pageable.getPageSize(),
                getUserCommentRespons
        );
    }

    @Transactional
    public UserProfileResponse imageUpdate(String loginId, MultipartFile file) throws IOException {
        User user = getByLoginId(loginId);

        String existingImageUrl = user.getImageUrl();

        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            s3Service.deleteFile(existingImageUrl);
        }

        String newImageUrl = s3Service.uploadFile(file);

        user.updateProfileImg(newImageUrl);

        return new UserProfileResponse(user.getId(), user.getImageUrl());
    }

    public UserDetailResponse findByUserId(String loginId, Long userId) {
        getByLoginId(loginId);

        User user = userRepository.findById(userId).orElseThrow( () ->
                new RuntimeException("존재하지 않는 유저 id : " + userId));

        return new UserDetailResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickName(),
                user.getImageUrl(),
                user.isLikePrivate());
    }

    public JwtTokens refresh(UserDetails userDetail, RefreshRequest request) {
        String refreshToken = request.refreshToken();
        if (jwtProvider.isValidToken(refreshToken)){
            RefreshTokenDetails refreshTokenDetails =
                    refreshTokenRepository.findByRefreshToken(refreshToken)
                            .orElseThrow(
                                    ()->new TokenNotFoundException("만료된 리프레시 토큰, 재로그인 바람")
                            );
            //{accessToken, refreshToken}
            String[] tokens = generateTokens(userDetail);
            return new JwtTokens(tokens[0], tokens[1]);
        }
        else {
            throw new TokenNotFoundException("유효하지 않은 토큰");
        }
    }

    private String[] generateTokens(UserDetails userDetail){
        String accessToken = jwtProvider.createToken(userDetail);
        String refreshToken = jwtProvider.createRefreshToken(userDetail);

        User user = getByLoginId(userDetail.getUsername());
        Date expirationDate = jwtProvider.getExpiration(refreshToken);
        LocalDateTime expiryDate = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshTokenDetails refreshTokenDetails = RefreshTokenDetails.builder()
                .refreshToken(refreshToken)
                .userId(user.getId())
                .expiryDate(expiryDate)
                .build();

        refreshTokenRepository.save(refreshTokenDetails);

        return new String[]{accessToken, refreshToken};
    }
}
