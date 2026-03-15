package stellarburgersapi.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgersapi.client.UserClient;
import stellarburgersapi.generator.UserGenerator;
import stellarburgersapi.model.User;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTest {

    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    public void successfulUserLogin() {
        User user = UserGenerator.getRandomUser();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.jsonPath().getString("accessToken");

        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    public void loginWithInvalidCredentialsShouldReturnError() {
        User user = UserGenerator.getRandomUser();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.jsonPath().getString("accessToken");

        User wrongUser = new User(
                user.getEmail(),
                "wrongPassword",
                user.getName()
        );

        Response response = userClient.loginUser(wrongUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
