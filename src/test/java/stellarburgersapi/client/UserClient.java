package stellarburgersapi.client;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgersapi.model.User;


import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class UserClient {

    private static final String REGISTER_PATH = "/api/auth/register"; // запрос насоздание пользователя



    @Step("Создать уникального пользователя")
    public Response createUser(User user) {
        return given()
                .contentType(JSON)
                .body(user)
                .post(REGISTER_PATH);
    }
private static final  String LOGIN_PATH = "/api/auth/login";
    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given()
                .contentType(JSON)
                .body(user)
                .post(LOGIN_PATH);
    }
    private static final String USER_PATH = "/api/auth/user";

    @Step("Изменение данных пользователя с авторизацией")
    public Response updateUserWithAuth(User user, String accessToken) {
        return given()
                .contentType(JSON)
                .header("Authorization", accessToken)
                .body(user)
                .patch(USER_PATH);
    }
    @Step("Изменение данных пользователя без авторизации")
    public Response updateUserWithoutAuth(User user) {
        return given()
                .contentType(JSON)
                .body(user)
                .patch(USER_PATH);
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .delete("/api/auth/user");
    }


}


