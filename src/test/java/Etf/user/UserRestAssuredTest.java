package Etf.user;

import Etf.DatabaseCleanup;
import Etf.user.dto.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;


import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestAssuredTest {

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
                .statusCode(401)
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
    void 비밀번호재설정Test() {
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

        UserPasswordResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest("123","321","321"))
                .when()
                .post("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserPasswordResponse.class);
    }

    @Test
    void 새비밀번호와확인비밀번호불일치Test() {
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

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest("123","321","3212"))
                .when()
                .post("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(500)
                .extract();
    }

    @Test
    void 같은비밀번호입력Test() {
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

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest("123","123","123"))
                .when()
                .post("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(500)
                .extract();
    }

    @Test
    void 공백입력() {
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

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest("123","",""))
                .when()
                .post("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(500)
                .extract();
    }

    @Test
    void 유저조회() {

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

        MypageResponse mypageResponse = RestAssured
                .given()
                .pathParam("userId", userResponse.id())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/users/{userId}")
                .then()
                .statusCode(200)
                .extract()
                .as(MypageResponse.class);

        assertThat(mypageResponse.id()).isEqualTo(userResponse.id());
        assertThat(mypageResponse.nickName()).isEqualTo(userResponse.nickName());
    }

}
