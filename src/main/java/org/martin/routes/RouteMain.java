package org.martin.routes;

import org.martin.util.Log;

public class RouteMain {
    public static void init(){
        Log.info("Initializing Route Main");

        PublicRoutes.init();
        ApiRoutes.init();
        AuthRoutes.init();
    }
}
