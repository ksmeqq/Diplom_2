package api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.stellarburgers.education_services.User;

public class LoginApi {
    @Step("Отправка запроса на вход")
    public Response sendPostRequestToLoginUser(User user) {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Проверка кода ответа")
    public void compareResponseStatusCode(Response response, int expectedCode){
        response.then().statusCode(expectedCode);
    }


}
