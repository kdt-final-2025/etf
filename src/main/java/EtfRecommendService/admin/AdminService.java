package EtfRecommendService.admin;

import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.admin.dto.AdminLoginResponse;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.security.CustomUserDetailService;
import EtfRecommendService.security.UserDetail;
import EtfRecommendService.user.exception.UserMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static EtfRecommendService.user.exception.ErrorMessages.USER_MISMATCH;


@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailService userDetailsService;

    public AdminLoginResponse login(AdminLoginRequest loginRequest) {
        Admin admin = adminRepository.findByLoginId(loginRequest.loginId()).orElseThrow(
                () -> new UserMismatchException(USER_MISMATCH));

        if (!admin.isSamePassword(loginRequest.password())) {
            throw new UserMismatchException(USER_MISMATCH);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.roles()+":"+admin.getLoginId());

        return new AdminLoginResponse(jwtProvider.createToken(userDetails));
    }
}
