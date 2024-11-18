package praktikum.endpoints.operators;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import praktikum.objects.responseobjects.UserResponse;

import static org.hamcrest.CoreMatchers.equalTo;

public class CheckResponse {
    @Step("Проверка кода и статуса ответа")
    public void checkStatusCode(Response response, int code) {
        Allure.addAttachment("Код и статус: ", response.getStatusLine());
        response.then().statusCode(code);
    }

    @Step("Проверка успешности запроса")
    public void checkSuccessStatus(Response response, String expectedValue) {
        MatcherAssert.assertThat(
                "Неверное значение в поле success",
                expectedValue,
                equalTo(response.body().as(UserResponse.class).getSuccess())
        );
    }
    @Step("Проверка тела (сообщения) ответа")
    public void checkMessageText(Response response, String expectedMessage) {
        MatcherAssert.assertThat(
                "Неверный текст в поле message",
                expectedMessage,
                equalTo(response.body().as(UserResponse.class).getMessage())
        );
    }
}
