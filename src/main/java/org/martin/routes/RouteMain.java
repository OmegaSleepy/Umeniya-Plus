package org.martin.routes;

import org.martin.util.Log;

import static spark.Spark.after;
import static spark.Spark.notFound;

/**
 * Main route initializer
 * **/
public class RouteMain {
    public static void init(){
        Log.info("Initializing Route Main");

        PublicRoutes.init();
        ApiRoutes.init();
        AuthRoutes.init();


        notFound((request, response) -> {
            response.redirect("/404");
            return null;
        });

        after((req, res) -> Log.info("Matched: " + req.pathInfo()));
    }
}
