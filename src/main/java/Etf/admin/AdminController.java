package Etf.admin;

import Etf.admin.dto.AdminLoginRequest;
import Etf.admin.dto.AdminLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public AdminLoginResponse login(@RequestBody AdminLoginRequest loginRequest) {
        return adminService.login(loginRequest);
    }


}
