package EtfRecommendService.admin;

import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.admin.dto.AdminLoginResponse;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.security.CustomUserDetailService;
import EtfRecommendService.security.RefreshTokenDetails;
import EtfRecommendService.security.RefreshTokenRepository;
import EtfRecommendService.security.UserDetail;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import EtfRecommendService.user.dto.UserLoginResponse;
import EtfRecommendService.user.exception.UserMismatchException;
import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static EtfRecommendService.user.exception.ErrorMessages.USER_MISMATCH;


@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AdminLoginResponse login(AdminLoginRequest loginRequest) {
        String identifier = loginRequest.roles().toUpperCase() + ":" + loginRequest.loginId();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.password());

        Authentication authentication = authenticationManager.authenticate(token);

        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        //{accessToken, refreshToken}
        String[] tokens = generateTokens(userDetail);

        Admin admin = getByLoginId(userDetail.getUsername());
        LocalDateTime expiryDate = jwtProvider
                .getExpirationFromRefreshToken(tokens[1])
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshTokenDetails refreshTokenDetails = RefreshTokenDetails.builder()
                .refreshToken(tokens[1])
                .userId(admin.getId())
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(refreshTokenDetails);

        return new AdminLoginResponse(tokens[0], tokens[1]);
    }

    private String[] generateTokens(UserDetails userDetail) {
        String accessToken = jwtProvider.createToken(userDetail);
        String refreshToken = jwtProvider.createRefreshToken(userDetail);
        return new String[]{accessToken, refreshToken};
    }

    private Admin getByLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 관리자"));
    }

}
