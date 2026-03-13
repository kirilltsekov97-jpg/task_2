package StellarBurgersAPI;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    public void createOrderWithoutAuthWithIngredients() {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        ));

        Response response = orderClient.createOrderWithoutAuth(body);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void createOrderWithAuthWithIngredients() {
        User user = UserGenerator.getRandomUser();
        Response createUserResponse = userClient.createUser(user);
        accessToken = createUserResponse.jsonPath().getString("accessToken");

        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        ));

        Response response = orderClient.createOrderWithAuth(body, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void createOrderWithoutIngredientsAndWithoutAuth() {
        Map<String, Object> body = new HashMap<>();

        Response response = orderClient.createOrderWithoutAuth(body);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithoutIngredientsWithAuth() {
        User user = UserGenerator.getRandomUser();
        Response createUserResponse = userClient.createUser(user);
        accessToken = createUserResponse.jsonPath().getString("accessToken");

        Map<String, Object> body = new HashMap<>();

        Response response = orderClient.createOrderWithAuth(body, accessToken);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithInvalidIngredientHash() {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", Collections.singletonList("invalid_hash"));

        Response response = orderClient.createOrderWithoutAuth(body);

        response.then()
                .statusCode(400);
    }
}
