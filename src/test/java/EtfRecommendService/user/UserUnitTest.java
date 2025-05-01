package EtfRecommendService.user;


import EtfRecommendService.user.exception.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;



import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserUnitTest {

    private User user;

    @BeforeEach
    void setUp() {
        Password password = new Password("현재비밀번호");
        user = new User("user1",password,"nickName",false);
    }

    @Test
    @DisplayName("비밀번호변경 성공")
    void 변경성공() {
        Password existingPassword = new Password("현재비밀번호");
        Password newPassword = new Password("새비밀번호");
        Password confirmNewPassword = new Password("새비밀번호");

        user.updatePassword(existingPassword,newPassword,confirmNewPassword);

        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("확인비밀번호와 새비밀번호 불일치")
    void 변경실패() {
        Password existingPassword = new Password("현재비밀번호");
        Password newPassword = new Password("새비밀번호");
        Password confirmNewPassword = new Password("다른비밀번호");

        PasswordMismatchException exception = assertThrows(
                PasswordMismatchException.class,
                () -> user.updatePassword(existingPassword, newPassword, confirmNewPassword)
        );
        assertEquals("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("입력받은 기존 비밀번호와 유저비밀번호불일치")
    void 변경실패2() {
        Password existingPassword = new Password("잘못된비밀번호");
        Password newPassword = new Password("새비밀번호");
        Password confirmNewPassword = new Password("새비밀번호");

        PasswordMismatchException exception = assertThrows(
                PasswordMismatchException.class,
                () -> user.updatePassword(existingPassword, newPassword, confirmNewPassword)
        );

        assertEquals("유저의 비밀번호와 입력받은 비밀번호가 같지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("변경할비밀번호가 동일")
    void 변경실패3() {
        // 준비
        Password existingPassword = new Password("현재비밀번호");
        Password newPassword = new Password("현재비밀번호");
        Password confirmNewPassword = new Password("현재비밀번호");

        // 실행 및 검증
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> user.updatePassword(existingPassword, newPassword, confirmNewPassword)
        );

        assertEquals("변경할 비밀번호가 같습니다.", exception.getMessage());
    }
}
