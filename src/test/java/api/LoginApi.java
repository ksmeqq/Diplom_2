package api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.stellar.burgers.education.services.User;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginApi {
    private Endpoints endpoints = new Endpoints();
    @Step("Отправка запроса на вход")
    public Response sendPostRequestToLoginUser(User user) {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(endpoints.LOGIN);
    }

    @Step("Проверка кода ответа")
    public void compareResponseStatusCode(Response response, int expectedCode){
        response.then().statusCode(expectedCode);
    }

    @Step("Проверка кода и сообщения ответа")
    public void compareResponseStatusCodeAndMessage(Response response, int expectedCode, String expectedMessage) {
        response.then()
                .statusCode(expectedCode)
                .and()
                .body("message", equalTo(expectedMessage));
    }
}
