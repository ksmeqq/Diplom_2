package tests;

import api.LoginApi;
import api.OrderApi;
import api.RegisterApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.education_services.Order;
import ru.stellarburgers.education_services.User;

import java.util.Collections;
import java.util.List;

public class CreateOrderTest {
    private Response response;
    private User user;
    private String email;
    private String password = "password";
    private String name = "name";
    private String accessToken;
    private List<String> ingredients;

    private RegisterApi registerApi;
    private LoginApi loginApi;
    private OrderApi orderApi;

    @Before
    public void setUp() {
        registerApi = new RegisterApi();
        loginApi = new LoginApi();
        orderApi = new OrderApi();

        email = "test-" + System.currentTimeMillis() + "@yandex.ru";
        user = new User(email, password, name);
        Response registerResponse = registerApi.sendPostRequestToCreateUser(user);
        accessToken = registerApi.getAccessToken(registerResponse);

        Response ingredientsResponse = orderApi.getIngredients();
        ingredients = orderApi.extractIngredientHashes(ingredientsResponse);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами возвращает 200")
    @Description("Авторизованный пользователь может создать заказ с валидными ингредиентами через /api/orders")
    public void createOrderWithAuthAndIngredients() {
        Order order = new Order(List.of(ingredients.get(0),
                ingredients.get(1)));
        response = orderApi.sendPostRequestToCreateOrder(order,
                accessToken);
        orderApi.compareResponseStatusCode(response, HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами возвращает 200")
    @Description("Неавторизованный пользователь может создать заказ с валидными ингредиентами через /api/orders")
    public void createOrderWithoutAuthWithIngredients() {
        Order order = new Order(List.of(ingredients.get(0),
                ingredients.get(1)));
        response = orderApi.sendPostRequestToCreateOrder(order);
        orderApi.compareResponseStatusCode(response, HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает 400")
    @Description("Ошибка 400 при попытке создать заказ с пустым списком ингредиентов")
    public void createOrderWithoutIngredients() {
        Order order = new Order(Collections.emptyList());
        response = orderApi.sendPostRequestToCreateOrder(order,
                accessToken);
        orderApi.compareResponseStatusCode(response,
                HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента возвращает 500")
    @Description("Ошибка 500 при попытке создать заказ с несуществующим хэш ингредиента")
    public void createOrderWithInvalidIngredientHash() {
        Order order = new Order(List.of("invalid_hash_12345"));
        response = orderApi.sendPostRequestToCreateOrder(order,
                accessToken);
        orderApi.compareResponseStatusCode(response,
                HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        registerApi.deleteUser(accessToken);
    }
}