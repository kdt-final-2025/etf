package EtfRecommendService.user;

import EtfRecommendService.S3Service;
import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.user.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @PostMapping()
    public UserResponse create(@RequestBody CreateUserRequest userRequest) {
        return userService.create(userRequest);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PatchMapping()
    public UserUpdateResponse profileUpdate(@LoginMember String auth, @RequestBody UserUpdateRequest updateRequest) {
        return userService.profileUpdate(auth,updateRequest);
    }

    @DeleteMapping()
    public UserDeleteResponse delete(@LoginMember String auth) {
        return userService.delete(auth);
    }

    @PostMapping("/me/password")
    public UserPasswordResponse passwordUpdate(@LoginMember String auth, @RequestBody UserPasswordRequest passwordRequest) {
        return userService.passwordUpdate(auth, passwordRequest);
    }

    @GetMapping("/{userId}")
    public MypageResponse findByUser(@LoginMember String auth, @PathVariable Long userId) {
        return userService.findByUser(auth, userId);
    }

    @PatchMapping("/image")
    public UserProfileResponse imageUpdate(@LoginMember String auth,
                                           @RequestPart(value = "images") MultipartFile file) throws IOException {
        return userService.imageUpdate(auth, file);
    }

}
