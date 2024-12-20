package praktikum.tests.order;

import com.github.javafaker.Faker;
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
import praktikum.objects.requestobjects.Order;
import praktikum.objects.responseobjects.IngredientsResponse;
import static org.apache.http.HttpStatus.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("create new order")
@Epic("Диплом. Тестирование API.")
@Feature("Создание нового заказа в сервисе Stellar Burgers")
@DisplayName("Тест # 4 - Создание нового заказа")
public class CreateOrderTests {
    private String email;
    private String password;
    private String name;
    private String token;
    private List<Ingredients> ingredients = new ArrayList<>();
    private final OrderAPIOperators orderAPI = new OrderAPIOperators();
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

        response = orderAPI.getIngredientList();
        checkResponse.checkStatusCode(response, SC_OK);
        ingredients = response.body().as(IngredientsResponse.class).getData();
    }

    @After
    @Step("Удаление данных после теста")
    public void clearAfterTests() {
        if (token != null) {
            checkResponse.checkStatusCode(userAPI.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и случайными ингредиентами")
    @Description("Тест API на создание заказа с авторизацией, используя случайные ингредиенты из списка. " +
            "Ожидаемый результат - заказ успешно создан.")
    public void createOrderWithAuthAndRandomIngredients() {
        int numberOfIngredients = faker.number().numberBetween(2, 6);
        List<String> selectedIngredients = new ArrayList<>();
        for (int i = 0; i < numberOfIngredients; i++) {
            Ingredients randomIngredient = ingredients.get(faker.number().numberBetween(0, ingredients.size()));
            selectedIngredients.add(randomIngredient.get_id());
        }
        Response response = orderAPI.createOrder(selectedIngredients, token);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Создание заказа без авторизации и случайными ингредиентами")
    @Description("Тест API на создание заказа с авторизацией, используя случайные ингредиенты из списка. " +
            "Ожидаемый результат - заказ успешно создан.")
    public void createOrderWithoutAuthAndRandomIngredients() {
        int numberOfIngredients = faker.number().numberBetween(2, 6);
        List<String> selectedIngredients = new ArrayList<>();
        for (int i = 0; i < numberOfIngredients; i++) {
            Ingredients randomIngredient = ingredients.get(faker.number().numberBetween(0, ingredients.size()));
            selectedIngredients.add(randomIngredient.get_id());
        }
        Response response = orderAPI.createOrder(new Order(selectedIngredients));

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
        //в документации нет информации об ожидаемом коде и статусе, но тест не падает если ожидать 200 ОК
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    @Description("Тест API на создание заказа с авторизацией, без добавления ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithAuthAndWithoutIngredients() {
        List<String> emptyIngredients = new ArrayList<>();
        Response response = orderAPI.createOrder(emptyIngredients, token);

        checkResponse.checkStatusCode(response, SC_BAD_REQUEST);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Тест API на создание заказа без авторизации, без добавления ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithoutAuthAndWithoutIngredients() {
        List<String> emptyIngredients = new ArrayList<>();
        Response response = orderAPI.createOrder(emptyIngredients);

        checkResponse.checkStatusCode(response, SC_BAD_REQUEST);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с неверным хешем ингредиентов")
    @Description("Тест API на создание заказа с авторизацией, с неверным хешем ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithoutAuthAndWithWrongHash() {
        List<String> testIngredients = Arrays.asList(
                faker.internet().uuid(),
                faker.internet().uuid());
        Response response = orderAPI.createOrder(testIngredients, token);

        checkResponse.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и с неверным хешем ингредиентов")
    @Description("Тест API на создание заказа без авторизации, с неверным хешем ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithAuthAndWithWrongHash() {
        List<String> testIngredients = Arrays.asList(
                faker.internet().uuid(),
                faker.internet().uuid());
        Response response = orderAPI.createOrder(testIngredients);

        checkResponse.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);
    }
}

