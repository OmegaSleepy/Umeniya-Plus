package omega.sleepy.controllers;

import omega.sleepy.exceptions.InvalidCredentials;
import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import spark.Request;
import spark.Response;

import java.awt.*;

import static omega.sleepy.controllers.AuthController.AUTH_COOKIE;
import static omega.sleepy.routes.PublicRoutes.getSimpleTemplate;
import static omega.sleepy.services.BlogService.validateToken;

public class PublicController {
    public static String loginInterface(Request request, Response response) {
        String token = request.cookie(AUTH_COOKIE);
        skipLogIn(response, token);
        return getSimpleTemplate("login", response);
    }

    public static String register(Request request, Response response) {
        String token = request.cookie(AUTH_COOKIE);
        skipLogIn(response, token);
        return getSimpleTemplate("register", response);
    }

    private static void skipLogIn(Response response, String token){
        if (token != null) {
            try{
                validateToken(token);
                response.redirect("/home");
            } catch (InvalidCredentials e) {
                response.removeCookie(AUTH_COOKIE);
                Log.error(e.getMessage());
            }
        }
    }

    public static String createBlog(Request request, Response response) {
        String token = request.cookie(AUTH_COOKIE);

        if (token == null) {
            response.status(403);
            response.redirect("/login");
            return null;
        }

        try {
            validateToken(token);
        } catch (InvalidCredentials e) {
            response.status(403);
            response.redirect("/login");
            return null;
        }


        return getSimpleTemplate("create_blog", response);
    }

    public static String userProfile(Request request, Response response) {
        response.type(MediaType.HTML.getValue());
        response.status(200);
        String username = request.params("username");
        Log.info(username);
        return username;
    }

    public static String thisUserProfile(Request request, Response response) {
        return null;
    }
}
