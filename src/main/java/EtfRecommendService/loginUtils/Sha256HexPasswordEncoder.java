package EtfRecommendService.loginUtils;

import org.springframework.security.crypto.password.PasswordEncoder;

public class Sha256HexPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return SecurityUtils.sha256EncryptHex2(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String rawPasswordHash = encode(rawPassword);
        return rawPasswordHash.equals(encodedPassword);
    }
}
