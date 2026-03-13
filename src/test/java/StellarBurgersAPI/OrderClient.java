package StellarBurgersAPI;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class OrderClient {

    private static final String ORDERS_PATH = "/api/orders";

    @Step("Созданиеь заказ без авторизации")
    public Response createOrderWithoutAuth(Map<String, Object> body) {
        return given()
                .contentType(JSON)
                .body(body)
                .post(ORDERS_PATH);
    }

    @Step("Создание заказ с авторизацией")
    public Response createOrderWithAuth(Map<String, Object> body, String accessToken) {
        return given()
                .contentType(JSON)
                .header("Authorization", accessToken)
                .body(body)
                .post(ORDERS_PATH);
    }

    @Step("Получение заказа пользователя")
    public Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .get(ORDERS_PATH);
    }

    @Step("Получить заказы пользователя без авторизации")
    public Response getUserOrdersWithoutAuth() {
        return given()
                .get(ORDERS_PATH);
    }
}
