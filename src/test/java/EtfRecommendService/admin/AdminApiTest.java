package EtfRecommendService.admin;

import EtfRecommendService.AcceptanceTest;
import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.user.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
public class AdminApiTest extends AcceptanceTest {

    @Autowired
    private AdminDataSeeder adminDataSeeder;

    @Test
    void adminLogin() {
        adminDataSeeder.seedAdmin();

        RestAssured.given().log().all()
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
                .statusCode(200);
    }

    @Test
    void adminLoginFail() {
        adminDataSeeder.seedAdmin();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(AdminLoginRequest
                        .builder()
                        .loginId("admin")
                        .password("password")
                        .roles("USER")
                        .build())
                .when()
                .post("/api/v1/admin/login")
                .then().log().all()
                .statusCode(401);
    }
}
