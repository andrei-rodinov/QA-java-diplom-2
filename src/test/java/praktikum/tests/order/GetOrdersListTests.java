package praktikum.tests.order;


import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.endpoints.operators.CheckResponse;
import praktikum.endpoints.operators.OrderAPIOperators;
import praktikum.endpoints.operators.UserAPIOperators;
import praktikum.objects.requestobjects.Ingredients;
import praktikum.objects.responseobjects.IngredientsResponse;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("get order list")
@Epic("Диплом. Тестирование API.")
@Feature("Получение списка заказов в сервисе Stellar Burgers")
@DisplayName("Тест # 5 - Получение списка заказов")
public class GetOrdersListTests {

    private String email;
    private String password;
    private String name;
    private String token;
    private final OrderAPIOperators orderAPI = new OrderAPIOperators();
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
        checkResponse.checkStatusCode(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
            token = userAPI.getToken(response);
        }

        response = orderAPI.getIngredientList();
        checkResponse.checkStatusCode(response, SC_OK);
        List<Ingredients> ingredients = response.body().as(IngredientsResponse.class).getData();

        int numberOfIngredients = RANDOM.nextInt(4) + 2; // от 2 до 5 ингредиентов
        List<String> selectedIngredients = new ArrayList<>();
        for (int i = 0; i < numberOfIngredients; i++) {
            Ingredients randomIngredient = ingredients.get(RANDOM.nextInt(ingredients.size()));
            selectedIngredients.add(randomIngredient.get_id());
        }

        response = orderAPI.createOrder(selectedIngredients, token);
        checkResponse.checkStatusCode(response, SC_OK);
    }

    @After
    @Step("Удаление данных после теста")
    public void clearAfterTests () {
        if (token == null)
            return;
        checkResponse.checkStatusCode(userAPI.deleteUser(token), SC_ACCEPTED);
    }

    @Test
    @DisplayName("Получение всех заказов")
    @Description("Тест API на получение списка заказов. " +
            "Ожидаемый результат - список заказов получен.")
    public void getAllOrdersIsSuccess() {
        Response response = orderAPI.getAllOrderList();

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    @Description("Тест API на получение списка заказов авторизованного пользователя. " +
            "Ожидаемый результат - список заказов получен.")
    public void getAuthUsersOrdersIsSuccess() {
        Response response = orderAPI.getOrderList(token);
        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованного пользователя")
    @Description("Тест API на получение списка заказов неавторизованного пользователя. " +
            "Ожидаемый результат - список заказов не получен, получено сообщение об ошибке.")
    public void getNotAuthUsersOrdersIsSuccess() {
        Response response = orderAPI.getOrderList("");
        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response,"You should be authorised");
    }
}