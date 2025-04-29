package EtfRecommendService.user;

import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.user.dto.*;
import Etf.S3Service;
import Etf.loginUtils.LoginMember;
import Etf.user.dto.*;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public UserProfileResponse imageUpdate(@LoginMember String auth,@RequestPart(value = "images") MultipartFile files) throws IOException {
        User user = userService.getByLoginId(auth); // 기존 사용자 정보 조회
        String oldImageUrl = user.getImageUrl();    // 기존 이미지 URL

        // 기존 이미지 삭제 후 새 이미지 업로드
        String newImageUrl = s3Service.replaceFile(files, oldImageUrl);

        return userService.imageUpdate(auth, newImageUrl);
    }

}
