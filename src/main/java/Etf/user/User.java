package Etf.user;

import Etf.loginUtils.SecurityUtils;
import Etf.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    private String password;

    private String nickName;

    private String imageUrl = "";

    private Boolean isDeleted = false;

    private String theme;

    // 활정 기간
    private LocalDate suspensionPeriod;

    // 제재되어 삭제된 댓글카운트
    private int deletedCommentCount;

    // 활정 당한 횟수
    private int suspensionCount;

    // 댓글, 구독목록 공개여부
    private Boolean isLikePrivate = false;

    protected User() {
    }

    public User(String loginId,
                String password,
                String nickName,
                Boolean isLikePrivate
                ) {
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.isLikePrivate = isLikePrivate;
    }

    public void findByPassword(String password) {
        if (!this.getPassword().equals(SecurityUtils.sha256EncryptHex2(password))) {
            throw new RuntimeException("비밀번호가 다릅니다.");
        }
    }

    public void profileUpdate(String nickName, Boolean isLikePrivate) {
        if (nickName != null) {
            this.nickName = nickName;
        }
        this.isLikePrivate = isLikePrivate;
    }

    public void deleteUser() {
        if (this.isDeleted) {
            throw new DeleteUserException("이미 삭제된 회원입니다.");
        }
        this.isDeleted = true;
    }

}
