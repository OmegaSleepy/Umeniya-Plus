package omega.sleepy.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import omega.sleepy.dao.UserDao;
import omega.sleepy.exceptions.InvalidCredentials;
import omega.sleepy.exceptions.InvalidPassword;
import omega.sleepy.exceptions.MalformedPassword;
import omega.sleepy.exceptions.UserAlreadyExists;
import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import spark.Request;
import spark.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static omega.sleepy.services.AuthService.createUser;
import static omega.sleepy.services.AuthService.login;
import static omega.sleepy.services.BlogService.validateToken;

public class AuthController {

    private static final JsonParser jsonParser = new JsonParser();
    private static final Gson gson = new Gson();
    public static final String AUTH_COOKIE = "auth_cookie";

    public static String logIn(Request request, Response response){
        String password = request.queryParams("password");
        String username = request.queryParams("username");

        try{
            login(username,password);
            generateCookie(response, username);

            response.redirect("/home");
        } catch (InvalidPassword e) {
            response.status(401);
            return "";
        }

        return "";
    }

    private static void generateCookie(Response response, String username) {
        String token = UUID.randomUUID().toString();
        long expiration = Instant.now().plus(Duration.ofDays(7)).getEpochSecond();
        Log.exec("Generated session with token " + token);

        UserDao.addSession(token, username, expiration);
        response.cookie("/", AUTH_COOKIE, token,60*60*24*7, false, true);
    }

    public static String signUp(Request request, Response response){

        JsonObject body = jsonParser.parse(request.body()).getAsJsonObject();
        String password = body.get("password").getAsString();
        String username = body.get("username").getAsString();



        try{
            createUser(username, password);
            generateCookie(response, username);
        } catch (MalformedPassword | UserAlreadyExists e) {
            response.status(400);
            return e.getMessage();
        }

        return "Valid SignUp";
    }

    public static String logout(Request request, Response response) {
        String token = request.cookie(AUTH_COOKIE);

        if (token != null) {
            UserDao.removeSession(token);
            response.removeCookie(AUTH_COOKIE);
        }

        response.redirect("/");
        return null;
    }

    public static String dashboard(Request request, Response response) {
        response.type(MediaType.TXT.getValue());
        String token = request.cookie(AUTH_COOKIE);
        try {
            validateToken(token);
            return "Valid";
        } catch (InvalidCredentials e) {
            Log.error(e.getMessage());
            return e.getMessage();
        }
    }
}
