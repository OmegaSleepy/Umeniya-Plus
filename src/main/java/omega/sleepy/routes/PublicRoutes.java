package omega.sleepy.routes;


import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;

import static spark.Spark.*;

public class PublicRoutes {
    public static void init(){
        Log.info("Public routes initializing...");

        get("/", (request, response) -> {
            response.type(MediaType.TXT.getValue());
            return "HELLO WORLD";
        });

        Log.info("All public rouses initialized");
    }
}
