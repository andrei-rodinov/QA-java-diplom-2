package praktikum.tests.user;


import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.endpoints.operators.CheckResponse;
import praktikum.endpoints.operators.UserAPIOperators;
import static org.apache.http.HttpStatus.*;

import java.security.SecureRandom;
import java.util.ArrayList;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("login user")
@Epic("Диплом. Тестирование API.")
@Feature("Логин пользователя в сервисе Stellar Burgers")
@DisplayName("Тест # 2 - Логин пользователя")

public class LoginUserTests {
    private String email;
    private String password;
    private String name;
    private ArrayList<String> tokens = new ArrayList<>();
    private final UserAPIOperators userAPI = new UserAPIOperators();
    private final CheckResponse checkResponse = new CheckResponse();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateRandomString(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            result.append(ALPHABET.charAt(index));
        }
        return result.toString();
    }

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        this.email = "m_" + generateRandomString(5) + "@ya.ru";
        this.password = "p_" + generateRandomString(5);
        this.name = "n_" + generateRandomString(6);

        Response response = userAPI.registerUser(email, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
    }

    @After
    @Step ("Удаление данных после теста")
    public void clearAfterTests() {
        if(tokens.isEmpty())
            return;
        for (String token: tokens) {
            checkResponse.checkStatusCode(userAPI.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Тест API логин пользователя. Ожидаемый результат - пользователь залогинен")
    public void loginUserIsSuccess() {
    Response response = userAPI.loginUser(email, password);

    checkResponse.checkStatusCode(response, SC_OK);
    checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Логин пользователя без email")
    @Description("Тест API логин пользователя без email. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithoutEmailIsFailed() {
        Response response = userAPI.loginUser(null, password);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Тест API логин пользователя без пароля. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithoutPasswordIsFailed() {
        Response response = userAPI.loginUser(email, null);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя c некорректным email")
    @Description("Тест API логин пользователя с некорректным email. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithIncorrectEmailIsFailed() {
        Response response = userAPI.loginUser(email + "qwe", password);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя c некорректным паролем")
    @Description("Тест API логин пользователя с некорректным паролем. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithIncorrectPasswordIsFailed() {
        Response response = userAPI.loginUser(email, password + "qwe");

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }
}
