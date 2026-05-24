package tests;

import api.LoginApi;
import api.RegisterApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.education_services.User;

public class LoginUserTest {
    private Response response;
    private User user;
    private String email;
    private String password = "password";
    private String name = "name";
    private String accessToken;
    private RegisterApi registerApi;
    private LoginApi loginApi;

    @Before
    public void setUp() {
        registerApi = new RegisterApi();
        loginApi = new LoginApi();
        email = "test-" + System.currentTimeMillis() + "@yandex.ru";
        user = new User(email, password, name);
        Response registerResponse = registerApi.sendPostRequestToCreateUser(user);
        accessToken = registerApi.getAccessToken(registerResponse);
    }

    @Test
    @DisplayName("Вход по email и пароль возвращает 200")
    @Description("Успешный вход пользователя по email и password на ручку /api/auth/login")
    public void loginUserSuccess() {
        user = new User(email, password);
        response = loginApi.sendPostRequestToLoginUser(user);
        loginApi.compareResponseStatusCode(response, HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Вход с неверным паролем возвращает 401")
    @Description("Ошибка 401 при попытке входа с правильным email, но неверным паролем")
    public void loginUserWithWrongPassword() {
        user = new User(email, "wrongPassword");
        response = loginApi.sendPostRequestToLoginUser(user);
        loginApi.compareResponseStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Вход с неверным email возвращает 401")
    @Description("Ошибка 401 при попытке входа с несуществующим email")
    public void loginUserWithWrongEmail() {
        user = new User("wrong-" + email, password);
        response = loginApi.sendPostRequestToLoginUser(user);
        loginApi.compareResponseStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Вход без email возвращает 401")
    @Description("Ошибка 401 при попытке входа без поля email")
    public void loginUserWithoutEmail() {
        User loginUser = new User();
        loginUser.setPassword(password);
        response = loginApi.sendPostRequestToLoginUser(loginUser);
        loginApi.compareResponseStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Вход без пароля возвращает 401")
    @Description("Ошибка 401 при попытке входа без поля password")
    public void loginUserWithoutPassword() {
        User loginUser = new User();
        loginUser.setEmail(email);
        response = loginApi.sendPostRequestToLoginUser(loginUser);
        loginApi.compareResponseStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
    }

    @After
    public void tearDown() {
        registerApi.deleteUser(accessToken);
    }
}
