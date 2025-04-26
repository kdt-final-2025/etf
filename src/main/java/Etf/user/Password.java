package Etf.user;

import Etf.loginUtils.SecurityUtils;
import lombok.Getter;

@Getter
public class Password {

    private final String password;

    public Password(String password) {
        this.password = SecurityUtils.sha256EncryptHex(password);
    }

    public void equalsPassword(String password) {
        if (!this.getPassword().equals(SecurityUtils.sha256EncryptHex(password))) {
            throw new PasswordMismatchException("비밀번호가 다릅니다.");
        }
    }
}
