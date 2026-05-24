package api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.stellarburgers.education_services.Order;

import java.util.List;

public class OrderApi {

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .when()
                .get("/api/ingredients");
    }

    @Step("Извлечение хешей ингредиентов из ответа")
    public List<String> extractIngredientHashes(Response response) {
        return response.path("data._id");
    }

    @Step("Отправка запроса на создание заказа с авторизацией")
    public Response sendPostRequestToCreateOrder(Order order, String
            accessToken) {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Отправка запроса на создание заказа без авторизации")
    public Response sendPostRequestToCreateOrder(Order order) {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Проверка кода ответа")
    public void compareResponseStatusCode(Response response, int
            expectedCode) {
        response.then().statusCode(expectedCode);
    }
}