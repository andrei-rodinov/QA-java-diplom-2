package praktikum.endpoints.httpclients;

import io.restassured.response.Response;
import praktikum.ServerAPIURLs;
import praktikum.objects.requestobjects.Order;

public class OrderHTTPClient extends BaseHTTPClient {

    public Response createOrder(Order order, String token) {
        return doPostRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.ORDERS,
                order,
                "application/json",
                token
        );
    }

    public Response createOrder(Order order) {
        return doPostRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.ORDERS,
                order,
                "application/json"
        );
    }


    public Response getIngredientList() {
        return doGetRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.INGREDIENTS
        );
    }

    public Response getOrderList(String token) {
        return doGetRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.ORDERS,
                token
        );
    }

    public Response getAllOrderList() {
        return doGetRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.ALL_ORDERS
        );
    }
}