package org.martin.routes;

import org.martin.controllers.AuthController;

import static spark.Spark.path;
import static spark.Spark.post;

public class AuthRoutes {
    public static void init(){
        path("/auth/credentials/", () ->{
            post("/log-in", AuthController::logIn);
            post("/sign-up", AuthController::signUp);
        });

        post("/auth/logout",AuthController::logout);

    }
}
