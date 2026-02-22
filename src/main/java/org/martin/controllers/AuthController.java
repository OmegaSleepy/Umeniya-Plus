package org.martin.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.martin.dao.UserDao;
import org.martin.dto.ExceptionDTO;
import org.martin.exceptions.InvalidCredentials;
import org.martin.exceptions.InvalidPassword;
import org.martin.services.AuthService;
import org.martin.util.Log;
import org.martin.util.enums.MediaType;
import spark.Request;
import spark.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.martin.controllers.ControllerUtil.forbitten;
import static org.martin.services.AuthService.*;

public class AuthController {

    private static final Gson gson = new Gson();
    public static final String AUTH_COOKIE = "auth_cookie";
    public static final String LOGIN_ERROR = "Потребителят не съществува или паролата е грешна!";

    public static Object logIn(Request request, Response response){
        JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);

        String password = jsonObject.get("password").getAsString();
        String username = jsonObject.get("username").getAsString();

        if (!AuthService.userExists(username)) {
            Log.error("Username " + username + " not found");
            response.status(400);
            return gson.toJson(new ExceptionDTO(LOGIN_ERROR));
        }

        try{
            login(username,password);
            generateCookie(response, username);
            response.redirect("/home");

        } catch (InvalidPassword e) {
            Log.error("Invalid password for " + username);
            response.status(400);
            return gson.toJson(new ExceptionDTO(LOGIN_ERROR));
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
        } catch (RuntimeException e) {
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

    public static Object changePfp(Request request, Response response) {
        String icon = request.params(":icon");
        String cookie =  request.cookie(AUTH_COOKIE);
        if (cookie == null) {
            return forbitten(response);
        }
        String username = AuthService.getUsernameByToken(cookie);
        if (username == null) {
            return forbitten(response);
        }
        AuthService.changeProfilePicture(username, icon);
        return "{\"status\":\"success\"}";
    }
}
