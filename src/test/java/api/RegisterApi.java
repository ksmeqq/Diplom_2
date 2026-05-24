package api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.stellarburgers.education_services.User;

public class RegisterApi {
    @Step("Отправка запроса на создание пользователя")
    public Response sendPostRequestToCreateUser(User user){
        return RestAssured
                .given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Проверка кода ответа")
    public void compareResponseStatusCode(Response response, int expectedCode){
        response.then().statusCode(expectedCode);
    }

    public String getAccessToken(Response response){
        return response.path("accessToken");
    }
    public void deleteUser(String accessToken){
        if (accessToken == null) {
            return;
        }
        RestAssured
                .given()
                .spec(ApiSpec.getRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user");
    }
}
