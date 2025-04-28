package Etf.admin;

import Etf.admin.dto.AdminLoginRequest;
import Etf.admin.dto.AdminLoginResponse;
import Etf.loginUtils.JwtProvider;
import Etf.user.PasswordMismatchException;
import Etf.user.UserMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;

    public AdminLoginResponse login(AdminLoginRequest loginRequest) {
        Admin admin = adminRepository.findByLoginId(loginRequest.loginId()).orElseThrow(
                () -> new UserMismatchException("찾을 수 없는 관리자 id : " + loginRequest.loginId()));

        if (loginRequest.password().isSamePassword(admin.getPassword())) {
            return new AdminLoginResponse(jwtProvider.createToken(admin.getLoginId()));
        }
        throw new UserMismatchException("회원을 찾을 수 없습니다.");
    }
}
