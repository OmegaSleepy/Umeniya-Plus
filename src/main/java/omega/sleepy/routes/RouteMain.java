package omega.sleepy.routes;

import omega.sleepy.util.Log;

public class RouteMain {
    public static void init(){
        Log.info("Initializing Route Main");

        PublicRoutes.init();
        ApiRoutes.init();
        AuthRoutes.init();

    }
}
