package praktikum.endpoints.httpclients;

import io.restassured.response.Response;
import praktikum.ServerAPIURLs;
import praktikum.objects.requestobjects.User;

public class UserHTTPClient extends BaseHTTPClient {

    public Response registerUser(User user) {
        return doPostRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.REGISTER_USER,
                user,
                "application/json"
        );
    }
    public Response deleteUser(String token) {
        return doDeleteRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.USER,
                token
        );
    }
    public Response loginUser(User user) {
        return doPostRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.LOGIN_USER,
                user,
                "application/json"
        );
    }
    public Response updateUser(User user, String token) {
        return doPatchRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.USER,
                user,
                "application/json",
                token
        );
    }
    public Response updateUser(User user) {
        return doPatchRequest(
                ServerAPIURLs.SERVER_NAME + ServerAPIURLs.USER,
                user,
                "application/json"
        );
    }
}