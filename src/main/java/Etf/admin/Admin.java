package Etf.admin;

import Etf.loginUtils.SecurityUtils;
import Etf.utils.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    private String password;

    protected Admin() {
    }

    public void findByPassword(String password) {
        if (!this.getPassword().equals(SecurityUtils.sha256EncryptHex(password))) {
            throw new RuntimeException("비밀번호가 다릅니다.");
        }
    }
}
