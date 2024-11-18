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
@Tag("register new user")
@Epic("Диплом. Тестирование API.")
@Feature("Создание нового пользователя в сервисе Stellar Burgers")
@DisplayName("Тест # 1 - Создание нового пользователя")

public class RegisterUserTests {
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
    @DisplayName("Регистрация нового пользователя")
    @Description("Тест API создание нового пользователя. Ожидаемый результат - пользователь создан")
    public void registerUserIsSuccess() {
        Response response = userAPI.registerUser(email, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Регистрация двух пользователей с одинаковыми данными")
    @Description("Тест API создание двух пользователей с одинаковыми данными. " +
            "Ожидаемый результат - одинаковых пользователей создать нельзя.")
    public void registerSameUserIsFailed() {
        Response responseX = userAPI.registerUser(email, password, name);
        Response responseY = userAPI.registerUser(email, password, name);
        if (responseX.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(responseX));
        }
        if (responseY.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(responseY));
        }

        checkResponse.checkStatusCode(responseY, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(responseY, "false");
        checkResponse.checkMessageText(responseY,"User already exists");
    }

    @Test
    @DisplayName("Регистрация пользователя без email")
    @Description("Тест API регистрация пользователя без email. Ожидаемый результат - пользователя без email создать нельзя")
    public void registerUserWithoutEmailIsFailed() {
        Response response = userAPI.registerUser(null, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    @Description("Тест API регистрация пользователя без пароля. Ожидаемый результат - пользователя без пароля создать нельзя")
    public void registerUserWithoutPasswordIsFailed() {
        Response response = userAPI.registerUser(email, null, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без имени")
    @Description("Тест API регистрация пользователя без имени. Ожидаемый результат - пользователя без имени создать нельзя")
    public void registerUserWithoutNameIsFailed() {
        Response response = userAPI.registerUser(email, null, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без данных")
    @Description("Тест API регистрация пользователя без данных. Ожидаемый результат - пользователя без данных создать нельзя")
    public void registerUserWithoutDataIsFailed() {
        Response response = userAPI.registerUser(null, null, null);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }
}
