package omega.sleepy.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import omega.sleepy.dao.UserDao;
import omega.sleepy.exceptions.InvalidPassword;
import omega.sleepy.exceptions.MalformedPassword;
import omega.sleepy.services.AuthService;
import org.eclipse.jetty.util.ajax.JSON;
import spark.Request;
import spark.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static omega.sleepy.services.AuthService.createUser;
import static omega.sleepy.services.AuthService.login;

public class AuthController {

    private static final JsonParser jsonParser = new JsonParser();
    private static final Gson gson = new Gson();

    public static String logIn(Request request, Response response){
        String password = request.queryParams("password");
        String username = request.queryParams("username");

        try{
            login(username,password);
            String token = UUID.randomUUID().toString();
            long expiration = Instant.now().plus(Duration.ofDays(7)).getEpochSecond();

            UserDao.addSession(token, username, expiration);
            response.cookie("auth_cookie", token,60*60*24*7, false, true);

            response.redirect("/home");

        } catch (InvalidPassword e) {
            response.status(401);
            return "";
        }

        return "";
    }

    public static String signUp(Request request, Response response){

        JsonObject body = jsonParser.parse(request.body()).getAsJsonObject();
        String password = body.get("password").getAsString();
        String username = body.get("username").getAsString();

        try{
            createUser(username, password);
        } catch (MalformedPassword e) {
            response.status(400);
            return e.getMessage();
        }

        return "Valid SignUp";
    }

    public static String logout(Request request, Response response) {
        String token = request.cookie("auth_cookie");

        if (token != null) {
            UserDao.removeSession(token);
            response.removeCookie("auth_cookie");
        }

        response.redirect("/");
        return null;
    }
}
