package EtfRecommendService.user;

import EtfRecommendService.S3Service;
import EtfRecommendService.loginUtils.JwtToken;
import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.user.dto.*;

import EtfRecommendService.user.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @PostMapping("/join")
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest userRequest) {
        UserResponse userResponse = userService.create(userRequest);
        return ResponseEntity.status(201).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
        UserLoginResponse login = userService.login(loginRequest);
        return ResponseEntity.ok(login);
    }

    @PostMapping("/api/refresh")
    public ResponseEntity<JwtToken> refresh(@RequestBody RefreshRequest request) {
        userService.refresh(request);
    }

    @Secured("USER")
    @PatchMapping("/users")
    public ResponseEntity<UserUpdateResponse> updateProfile(@LoginMember String auth, @RequestBody UserUpdateRequest updateRequest) {
        UserUpdateResponse userUpdateResponse = userService.UpdateProfile(auth, updateRequest);
        return ResponseEntity.ok(userUpdateResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/users")
    public ResponseEntity<Void> delete(@LoginMember String auth) {
        userService.delete(auth);
        return ResponseEntity.noContent().build();
    }

    @Secured("USER")
    @PatchMapping("/users/me/password")
    public ResponseEntity<UserPasswordResponse> updatePassword(@LoginMember String auth, @RequestBody UserPasswordRequest passwordRequest) {
        if (!passwordRequest.newPassword().isSamePassword(passwordRequest.confirmNewPassword())) {
            throw new PasswordMismatchException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        UserPasswordResponse userPasswordResponse = userService.updatePassword(auth, passwordRequest);
        return ResponseEntity.ok(userPasswordResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/comments/{userId}")
    public ResponseEntity<UserPageResponse> findUserComments(
            @LoginMember String auth,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        UserPageResponse userPageResponse = userService.findUserComments(auth, userId, pageable);
        return ResponseEntity.ok(userPageResponse);
    }

    @Secured("USER")
    @PatchMapping("/users/image")
    public ResponseEntity<UserProfileResponse> imageUpdate(@LoginMember String auth,
                                           @RequestPart(value = "images") MultipartFile file) throws IOException {
        UserProfileResponse userProfileResponse = userService.imageUpdate(auth, file);
        return ResponseEntity.ok(userProfileResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDetailResponse> findByUserId(@LoginMember String auth, @PathVariable Long userId) {
        UserDetailResponse userDetailResponse = userService.findByUserId(auth, userId);
        return ResponseEntity.ok(userDetailResponse);
    }

}
