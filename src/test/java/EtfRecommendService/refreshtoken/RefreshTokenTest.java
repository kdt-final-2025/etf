package EtfRecommendService.refreshtoken;

import EtfRecommendService.AcceptanceTest;
import EtfRecommendService.admin.AdminDataSeeder;
import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.user.RefreshRequest;
import EtfRecommendService.user.dto.UserUpdateRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ActiveProfiles("test")
public class RefreshTokenTest extends AcceptanceTest {

    @Autowired
    private AdminDataSeeder adminDataSeeder;

    @Test
    void 리프레쉬토큰_재발급() {
        adminDataSeeder.seedAdmin();

        String refreshToken = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(AdminLoginRequest
                        .builder()
                        .loginId("admin")
                        .password("password")
                        .roles("ADMIN")
                        .build())
                .when()
                .post("/api/v1/admin/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("refreshToken");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(RefreshRequest.builder()
                        .refreshToken(refreshToken)
                        .build())
                .when()
                .post("/api/v1/refresh")
                .then().log().all()
                .statusCode(200);

    }
}
