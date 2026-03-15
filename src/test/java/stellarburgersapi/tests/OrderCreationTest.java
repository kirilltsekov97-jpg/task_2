package stellarburgersapi.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgersapi.client.IngredientClient;
import stellarburgersapi.client.OrderClient;
import stellarburgersapi.client.UserClient;
import stellarburgersapi.generator.UserGenerator;
import stellarburgersapi.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTest extends BaseTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private IngredientClient ingredientClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredientClient = new IngredientClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Создание заказа без автаризации с валидными ингредиентами должно быть успешным")
    public void createOrderWithoutAuthWithIngredients() {
        Response ingredientsResponse = ingredientClient.getIngredients();
        String firstIngredientId = ingredientsResponse.jsonPath().getString("data[0]._id");
        String secondIngredientId = ingredientsResponse.jsonPath().getString("data[1]._id");

        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", Arrays.asList(firstIngredientId, secondIngredientId));

        Response response = orderClient.createOrderWithoutAuth(body);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа с авторизацией и валидными ингредиентами должно быть успешным")
    public void createOrderWithAuthWithIngredients() {
        User user = UserGenerator.getRandomUser();
        Response createUserResponse = userClient.createUser(user);
        accessToken = createUserResponse.jsonPath().getString("accessToken");

        Response ingredientsResponse = ingredientClient.getIngredients();
        String firstIngredientId = ingredientsResponse.jsonPath().getString("data[0]._id");
        String secondIngredientId = ingredientsResponse.jsonPath().getString("data[1]._id");

        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", Arrays.asList(firstIngredientId, secondIngredientId));

        Response response = orderClient.createOrderWithAuth(body, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без ингредиентов и без авторизации должно возвращать ошибку")
    public void createOrderWithoutIngredientsAndWithoutAuth() {
        Map<String, Object> body = new HashMap<>();

        Response response = orderClient.createOrderWithoutAuth(body);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа без ингредиентов с авторизацией должно возвращать ошибку")
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
    @Description("Создание заказа с невалидным хешем ингредиента должно возвращать ошибку")
    public void createOrderWithInvalidIngredientHash() {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", Collections.singletonList("invalid_hash"));

        Response response = orderClient.createOrderWithoutAuth(body);

        response.then()
                .statusCode(400);
    }
}
