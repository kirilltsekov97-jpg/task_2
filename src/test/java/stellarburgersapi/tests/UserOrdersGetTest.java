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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserOrdersGetTest extends BaseTest{

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private IngredientClient ingredientClient;

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
    @Description("Получение заказов авторизованным пользователем")
    public void getOrdersWithAuthorization() {
        User user = UserGenerator.getRandomUser();
        Response createUserResponse = userClient.createUser(user);
        accessToken = createUserResponse.jsonPath().getString("accessToken");

        Response ingredientsResponse = ingredientClient.getIngredients();
        String firstIngredientId = ingredientsResponse.jsonPath().getString("data[0]._id");
        String secondIngredientId = ingredientsResponse.jsonPath().getString("data[1]._id");

        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", Arrays.asList(firstIngredientId, secondIngredientId));

        orderClient.createOrderWithAuth(body, accessToken);

        Response response = orderClient.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @Description("Получение заказов неавторизованным пользователем должно возвращать ошибку")
    public void getOrdersWithoutAuthorization() {
        Response response = orderClient.getUserOrdersWithoutAuth();

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
