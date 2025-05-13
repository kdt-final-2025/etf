package EtfRecommendService;

import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.security.UserDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenTest{

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void name() {
        UserDetails userDetails = new UserDetail("test-name", "test-pw", List.of(new SimpleGrantedAuthority("USER")));
        String token = jwtProvider.createToken(userDetails);
        System.out.println(token);
        Assertions.assertThat(jwtProvider.isValidToken(token)).isTrue();
    }
}
