package tests;

import api.RegisterApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellar.burgers.education.services.User;


public class CreateUserTest {
    private Response response;
    private String email;
    private String password = "password";
    private String name = "name";
    private RegisterApi registerApi;

    @Before
    public void setup() {
        registerApi = new RegisterApi();
        email = "test-" + System.currentTimeMillis() + "@yandex.ru";
    }

    @Test
    @DisplayName("Регистрация нового пользователя возвращает 200")
    @Description("Успешная регистрация пользователя через ручку /api/auth/register возвращает код 200")
    public void createUserTest() {
        User user = new User(email, password, name);
        response = registerApi.sendPostRequestToCreateUser(user);
        registerApi.compareResponseStatusCode(response, HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Повторная регистрация того же пользователя возвращает 403")
    @Description("Ошибка 403 если зарегистрировать того же пользователя на ручку /api/auth/register")
    public void createUserWhoIsAlreadyRegisteredTest() {
        User user = new User(email, password, name);
        response = registerApi.sendPostRequestToCreateUser(user);
        Response response1 = registerApi.sendPostRequestToCreateUser(user);
        registerApi.compareResponseStatusCodeAndMessage(response1, HttpStatus.SC_FORBIDDEN, "User already exists");
    }

    @Test
    @DisplayName("Регистрация пользователя без email возвращает 403")
    @Description("Ошибка 403 если зарегистрировать пользователя без email на ручку /api/auth/register")
    public void createUserWithoutEmailTest() {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        response = registerApi.sendPostRequestToCreateUser(user);
        registerApi.compareResponseStatusCodeAndMessage(response, HttpStatus.SC_FORBIDDEN, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля возвращает 403")
    @Description("Ошибка 403 если зарегистрировать пользователя без пароля на ручку /api/auth/register")
    public void createUserWithoutPasswordTest() {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        response = registerApi.sendPostRequestToCreateUser(user);
        registerApi.compareResponseStatusCodeAndMessage(response, HttpStatus.SC_FORBIDDEN, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без имени возвращает 403")
    @Description("Ошибка 403 если зарегистрировать пользователя без имени на ручку /api/auth/register")
    public void createUserWithoutNameTest() {
        User user = new User();
        user.setPassword(password);
        user.setEmail(email);
        response = registerApi.sendPostRequestToCreateUser(user);
        registerApi.compareResponseStatusCodeAndMessage(response, HttpStatus.SC_FORBIDDEN, "Email, password and name are required fields");
    }

    @After
    public void tearDown() {
        registerApi.deleteUser(registerApi.getAccessToken(response));
    }
}
