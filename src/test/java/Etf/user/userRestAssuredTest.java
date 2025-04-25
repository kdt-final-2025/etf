package Etf.user;

import Etf.DatabaseCleanup;
import Etf.user.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class userRestAssuredTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() throws IOException {
        databaseCleanup.execute();
        RestAssured.port = port;
    }

    @Test
    void 멤버생성Test() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1","123","nick1",false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 로그인Test() {

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        UserLoginResponse loginResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1","123"))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserLoginResponse.class);
    }

    @Test
    void 다른비밀번호Test() {

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "1234"))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(500)
                .extract()
                .jsonPath()
                .getString("token");
    }

    @Test
    void 회원수정Test() {

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123"))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)  // 400에서 200으로 수정
                .extract()
                .jsonPath()
                .getString("token");

        UserUpdateRequest updateRequest = new UserUpdateRequest("newNick",false);

        UserUpdateResponse updateResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
                .when()
                .patch("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserUpdateResponse.class);
    }

    @Test
    void 회원삭제Test() {
        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123"))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        UserDeleteResponse deleteResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserDeleteResponse.class);
    }

    @Test
    void 삭제재요청Test() {

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123"))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        UserDeleteResponse deleteResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserDeleteResponse.class);

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users")
                .then().log().all()
                .statusCode(400)
                .extract();
    }
}
