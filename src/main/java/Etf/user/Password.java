package Etf.user;

import Etf.loginUtils.SecurityUtils;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Password {

    private String password;

    public Password(String password) {
        if (SecurityUtils.sha256EncryptHex(password).equals(SecurityUtils.sha256EncryptHex(""))) {
            throw new RuntimeException("비밀번호가 공백이면 안됩니다.");
        }
        this.password = SecurityUtils.sha256EncryptHex(password);
    }

    public void equalsPassword(String password) {
        if (!this.getPassword().equals(SecurityUtils.sha256EncryptHex(password))) {
            throw new PasswordMismatchException("비밀번호가 다릅니다.");
        }
    }

    public void isSamePassword(Password password) {
        if (this.getPassword().equals(password.getPassword())) {
            throw new RuntimeException("변경할 비밀번호가 같습니다.");
        }
    }


}
