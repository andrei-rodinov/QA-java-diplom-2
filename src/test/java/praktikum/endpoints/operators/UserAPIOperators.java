package praktikum.endpoints.operators;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import praktikum.objects.requestobjects.User;
import praktikum.objects.responseobjects.UserResponse;
import praktikum.endpoints.httpclients.UserHTTPClient;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.Matchers.equalTo;

public class UserAPIOperators extends UserHTTPClient {

    @Step("Создание нового пользователя")
    public Response registerUser(String email, String password, String name) {
        return super.registerUser(new User(email, password, name));
    }

    @Step("Получение токена авторизации")
    public String getToken(Response response) {
        String token = response.body().as(UserResponse.class).getAccessToken().split(" ")[1];
        Allure.addAttachment("Код и статус: ", response.getStatusLine());
        Allure.addAttachment("Токен: ", token);
        return token;
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        return super.loginUser(new User(email, password));
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return super.deleteUser(token);
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(String email, String password, String name, String token) {
        return super.updateUser(new User(email, password, name), token);
    }

    @Step("Обновление данных пользователя без токена")
    public Response updateUser(String email, String password, String name) {
        return super.updateUser(new User(email, password, name));
    }

    @Step("Проверка данных пользователя")
    public void checkUserData(Response response, String expectedEmail, String expectedPassword, String expectedName) {
        User currentUser = response.body().as(UserResponse.class).getUser();
        Allure.addAttachment("Новый пользователь", currentUser.toString());

        MatcherAssert.assertThat("Email не совпадает", currentUser.getEmail(), equalTo(expectedEmail));
        MatcherAssert.assertThat("Имя не совпадает", currentUser.getName(), equalTo(expectedName));

        new CheckResponse().checkStatusCode(loginUser(expectedEmail, expectedPassword), SC_OK);
    }
}
