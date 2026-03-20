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

public class UpdateUserDataTest extends BaseTest{

    private UserClient userClient;
    private String accessToken;
    private User user;

    @Before
    public void setUp() {

        userClient = new UserClient();

        user = UserGenerator.getRandomUser();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Изменение email пользователя с авторизацией должно быть успешным")
    public void updateUserEmailWithAuthorization() {
        User updatedUser = new User(
                "updated" + System.currentTimeMillis() + "@mail.ru",
                user.getPassword(),
                user.getName()
        );

        Response response = userClient.updateUserWithAuth(updatedUser, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @Description("Изменение имени пользователя с авторизацией должно быть успешным")
    public void updateUserNameWithAuthorization() {
        User updatedUser = new User(
                user.getEmail(),
                user.getPassword(),
                "NewName"
        );

        Response response = userClient.updateUserWithAuth(updatedUser, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @Description("Изменение пароля пользователя с авторизацией должно быть успешным")
    public void updateUserPasswordWithAuthorization() {
        User updatedUser = new User(
                user.getEmail(),
                "newPassword123",
                user.getName()
        );

        Response response = userClient.updateUserWithAuth(updatedUser, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @Description("Изменение данных пользователя без авторизации должно возвращать ошибку")
    public void updateUserDataWithoutAuthorization() {
        User updatedUser = new User(
                "newemail@mail.ru",
                "newPassword123",
                "NewName"
        );

        Response response = userClient.updateUserWithoutAuth(updatedUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
