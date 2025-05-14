package EtfRecommendService.user;

import EtfRecommendService.S3Service;
import EtfRecommendService.admin.Admin;
import EtfRecommendService.admin.AdminRepository;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.loginUtils.JwtTokens;
import EtfRecommendService.security.RefreshTokenDetails;
import EtfRecommendService.security.RefreshTokenRepository;
import EtfRecommendService.security.TokenNotFoundException;
import EtfRecommendService.security.UserDetail;
import EtfRecommendService.user.dto.*;
import EtfRecommendService.user.exception.UserMismatchException;
import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final AdminRepository adminRepository;

    public User getByLoginId(String loginId) {
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId).orElseThrow(
                () -> new UserMismatchException(USER_MISMATCH));
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
        String identifier = loginRequest.role().toUpperCase() + ":" + loginRequest.loginId();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.password());

        Authentication authentication = authenticationManager.authenticate(token);

        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        //{accessToken, refreshToken}
        String[] tokens = generateTokens(userDetail);

        User user = getByLoginId(userDetail.getUsername());
        LocalDateTime expiryDate = jwtProvider
                .getExpirationFromRefreshToken(tokens[1])
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshTokenDetails refreshTokenDetails = RefreshTokenDetails.builder()
                .refreshToken(tokens[1])
                .userId(user.getId())
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(refreshTokenDetails);

        return new UserLoginResponse(tokens[0], tokens[1]);
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

        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("존재하지 않는 유저 id : " + userId));

        return new UserDetailResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickName(),
                user.getImageUrl(),
                user.isLikePrivate());
    }

    public JwtTokens refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();
        if (jwtProvider.isValidToken(refreshToken, false)) {
            RefreshTokenDetails refreshTokenDetails =
                    refreshTokenRepository.findByRefreshToken(refreshToken)
                            .orElseThrow(
                                    () -> new TokenNotFoundException("만료된 리프레시 토큰, 재로그인 바람")
                            );
            String username = jwtProvider.getSubjectFromRefresh(refreshToken);
            List<SimpleGrantedAuthority> roles = jwtProvider.getRolesFromRefresh(refreshToken)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UserDetails userDetails = new UserDetail(username,"", roles);

            //{accessToken, refreshToken}
            String[] tokens = generateTokens(userDetails);

            Long userId;
            if ("ROLE_ADMIN".equalsIgnoreCase(roles.get(0).getAuthority())){
                userId = getByAdminLoginId(username).getId();
            }
            else {
                userId = getByLoginId(username).getId();
            }
            LocalDateTime expiryDate = jwtProvider
                    .getExpirationFromRefreshToken(tokens[1])
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            RefreshTokenDetails newRefresh = RefreshTokenDetails.builder()
                    .refreshToken(tokens[1])
                    .userId(userId)
                    .expiryDate(expiryDate)
                    .build();
            refreshTokenRepository.deleteById(refreshTokenDetails.getId());
            refreshTokenRepository.save(newRefresh);
            return new JwtTokens(tokens[0], tokens[1]);
        } else {
            throw new TokenNotFoundException("유효하지 않은 토큰");
        }
    }

    public Admin getByAdminLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 관리자"));
    }

    private String[] generateTokens(UserDetails userDetail) {
        String accessToken = jwtProvider.createToken(userDetail);
        String refreshToken = jwtProvider.createRefreshToken(userDetail);
        return new String[]{accessToken, refreshToken};
    }
}
