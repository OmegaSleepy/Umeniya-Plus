package org.martin.controllers;

import org.martin.exceptions.InvalidCredentials;
import org.martin.routes.PublicRoutes;
import org.martin.services.AuthService;
import org.martin.util.Log;
import org.martin.util.enums.MediaType;
import spark.Request;
import spark.Response;

import static org.martin.controllers.AuthController.AUTH_COOKIE;
import static org.martin.controllers.ControllerUtil.missingResourcePage;
import static org.martin.routes.PublicRoutes.getSimpleTemplate;
import static org.martin.services.AuthService.validateToken;

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
        if(username == null){
            return missingResourcePage(response);
        }
        if(!AuthService.userExists(username)){
            return missingResourcePage(response);
        }

        String token = request.cookie(AUTH_COOKIE);
        if (token != null) {
            String user = AuthService.getUsernameByToken(token);
            if (user != null) {
                if(user.equals(username)){
                    response.redirect("/dashboard");
                    return "";
                }
            }
        }


        return PublicRoutes.getSimpleTemplate("user", response);
    }

    public static Object dashboard(Request request, Response response) {
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

        return getSimpleTemplate("dashboard", response);
    }

    public static Object shop(Request request, Response response) {
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

        return getSimpleTemplate("shop", response);
    }
}
