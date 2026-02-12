package omega.sleepy.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import omega.sleepy.dao.UserDao;
import omega.sleepy.dto.ExceptionDTO;
import omega.sleepy.exceptions.InvalidCredentials;
import omega.sleepy.exceptions.InvalidPassword;
import omega.sleepy.exceptions.MalformedPassword;
import omega.sleepy.exceptions.UserAlreadyExists;
import omega.sleepy.services.AuthService;
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

    private static String generateCookie(Response response, String username) {

        if (!AuthService.userExists(username)) {
            response.status(401);
            return new ExceptionDTO("Invalid username or password").toString();
        }

        String token = UUID.randomUUID().toString();
        long expiration = Instant.now().plus(Duration.ofDays(7)).getEpochSecond();
        Log.exec("Generated session with token " + token);

        UserDao.addSession(token, username, expiration);
        response.cookie("/", AUTH_COOKIE, token,60*60*24*7, false, true);
        return "";
    }

    public static Object signUp(Request request, Response response){

        JsonObject body = gson.fromJson(request.body(), JsonObject.class);
        String password = body.get("password").getAsString();
        String username = body.get("username").getAsString();

        try{
            createUser(username, password);
            generateCookie(response, username);
        } catch (MalformedPassword | UserAlreadyExists e) {
            response.status(400);
            Log.exec(e.toString());
            return gson.toJson(new ExceptionDTO(e.getMessage()));
        }

        return "Success";
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
            response.redirect("/login");
            return "";
        }
    }
}
