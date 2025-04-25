package Etf.user;

import Etf.loginUtils.LoginMember;
import Etf.user.dto.*;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/api/v1/users")
    public UserResponse create(@RequestBody CreateUserRequest userRequest) {
        return userService.create(userRequest);
    }

    @PostMapping("/api/v1/users/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PatchMapping("/api/v1/users")
    public UserUpdateResponse profileUpdate(@LoginMember String auth, @RequestBody UserUpdateRequest updateRequest) {
        return userService.profileUpdate(auth,updateRequest);
    }

    @DeleteMapping("/api/v1/users")
    public UserDeleteResponse delete(@LoginMember String auth) {
        return userService.delete(auth);
    }

    @PostMapping("/api/v1/users/me/password")
    public UserPasswordResponse passwordUpdate(@LoginMember String auth, @RequestBody UserPasswordRequest passwordRequest) {
        return userService.passwordUpdate(auth, passwordRequest);
    }

    @GetMapping("/api/v1/users/{userId}")
    public MypageResponse findByUser(@LoginMember String auth, @PathVariable Long userId) {
        return userService.findByUser(auth, userId);
    }
}
