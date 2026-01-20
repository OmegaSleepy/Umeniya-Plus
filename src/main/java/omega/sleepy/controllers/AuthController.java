package omega.sleepy.controllers;

import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;

public class AuthController {
    public static void main(String[] args) {
        String hash = BCrypt.hashpw("i-love-birds",BCrypt.gensalt(10));

        if(BCrypt.checkpw("i-love-pigeons", hash)) {
            System.out.println("Password is valid");
        } else {
            System.out.println("Password is invalid");
        }

        System.out.println(hash);
    }

    public static String logIn(Request request, Response response){
        System.out.println(request.queryParams("password"));
        return "ok";
    }

    public static String signUp(Request request, Response response){
        System.out.println(request.queryParams("password"));
        return "ok";
    }

}
