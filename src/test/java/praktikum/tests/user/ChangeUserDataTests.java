package praktikum.tests.user;

import com.github.javafaker.Faker;
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

import java.util.ArrayList;


@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("change user data")
@Epic("Диплом. Тестирование API.")
@Feature("Редактирование данных пользователя в сервисе Stellar Burgers")
@DisplayName("Тест # 3 - Редактирование данных пользователя")
public class ChangeUserDataTests {
    private String email;
    private String password;
    private String name;
    private String token;
    private final ArrayList<String> tokens = new ArrayList<>();
    private final UserAPIOperators userAPI = new UserAPIOperators();
    private final CheckResponse checkResponse = new CheckResponse();
    private final Faker faker = new Faker();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        this.email = faker.internet().safeEmailAddress();
        this.password = faker.letterify("?????????");
        this.name = faker.name().firstName();

        Response response = userAPI.registerUser(email, password, name);
        checkResponse.checkStatusCode(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
           token = userAPI.getToken(response);
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
    @DisplayName("Изменение email пользователя: с авторизацией")
    @Description("Тест API редактирование email авторизованного пользователя. " +
            "Ожидаемый результат - email изменен")
    public void changeUserEmailWithAuthIsSuccess() {
        String newEmail = "m_" + faker.internet().safeEmailAddress();

        Response response = userAPI.updateUser(newEmail, password, name, token);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
        userAPI.checkUserData(response, newEmail, password, name);
    }

    @Test
    @DisplayName("Изменение пароля пользователя: с авторизацией")
    @Description("Тест API редактирование пароля авторизованного пользователя. " +
            "Ожидаемый результат - пароль изменен")
    public void changeUserPasswordWithAuthIsSuccess() {
        String newPassword = "p_" + faker.letterify("?????????");

        Response response = userAPI.updateUser(email, newPassword, name, token);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
        userAPI.checkUserData(response, email, newPassword, name);
    }

    @Test
    @DisplayName("Изменение имени пользователя: с авторизацией")
    @Description("Тест API редактирование имени авторизованного пользователя. " +
            "Ожидаемый результат - имя изменено")
    public void changeUserNameWithAuthIsSuccess() {
        String newName = "n_" + faker.name().firstName();

        Response response = userAPI.updateUser(email, password, newName, token);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
        userAPI.checkUserData(response, email, password, newName);
    }

    @Test
    @DisplayName("Изменение email пользователя: без авторизации")
    @Description("Тест API редактирование email неавторизованного пользователя. " +
            "Ожидаемый результат - email не изменен, получено сообщение об ошибке")
    public void changeUserEmailWithoutAuthIsSuccess() {
        String newEmail = "m_" + faker.internet().safeEmailAddress();

        Response response = userAPI.updateUser(newEmail, password, name);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "You should be authorised");
    }

    @Test
    @DisplayName("Изменение пароля пользователя: без авторизации")
    @Description("Тест API редактирование пароля неавторизованного пользователя. " +
            "Ожидаемый результат - пароль не изменен, получено сообщение об ошибке")
    public void changeUserPasswordWithoutAuthIsSuccess() {
        String newPassword = "p_" + faker.letterify("?????????");;

        Response response = userAPI.updateUser(email, newPassword, name);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "You should be authorised");
    }

    @Test
    @DisplayName("Изменение имени пользователя: без авторизации")
    @Description("Тест API редактирование имени неавторизованного пользователя. " +
            "Ожидаемый результат - имя не изменено, получено сообщение об ошибке")
    public void changeUserNameWithoutAuthIsSuccess() {
        String newName = "n_" + faker.name().firstName();

        Response response = userAPI.updateUser(email, password, newName);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "You should be authorised");
    }
}
