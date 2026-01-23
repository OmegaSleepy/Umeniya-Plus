package omega.sleepy.controllers;

import omega.sleepy.exceptions.MalformedPassword;
import spark.Request;
import spark.Response;

import static omega.sleepy.services.AuthService.createUser;

public class AuthController {

    public static String logIn(Request request, Response response){
        String password = request.queryParams("password");
        String username = request.queryParams("username");

        createUser(username, password);

        //return profile cookie

        return "";
    }

    public static String signUp(Request request, Response response){
        String password = request.queryParams("password");
        String username = request.queryParams("username");

        try{
            createUser(username, password);
        } catch (MalformedPassword e) {
            response.status(400);
            return e.getMessage();
        }

        return "Valid SignUp";
    }
}
