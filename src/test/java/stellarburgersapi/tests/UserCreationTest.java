package stellarburgersapi.tests;
import io.qameta.allure.Description;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgersapi.client.UserClient;
import stellarburgersapi.generator.UserGenerator;
import stellarburgersapi.model.User;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserCreationTest extends BaseTest {


    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {

        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Создание уникального пользователя должно быть успешным")
    public void successfulUserCreation() {
        User user = UserGenerator.getRandomUser();
        Response response = userClient.createUser(user);
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @Description("Создание уже зарегистрированного пользователя должно возвращать ошибку")
    public void createUserAlreadyRegistered() {
        User user = UserGenerator.getRandomUser();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.jsonPath().getString("accessToken");

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Создание пользователя без заполнения обязательного поля должно возвращать ошибку")
    public void createUserWithoutRequiredField() {
        User user = new User(
                null,
                "passss12312",
                "Dom Torr"
        );

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
