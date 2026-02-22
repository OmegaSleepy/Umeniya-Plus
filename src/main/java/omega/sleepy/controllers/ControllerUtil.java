package omega.sleepy.controllers;

import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import spark.Response;

public class ControllerUtil {
    public static String forbitten(Response response) {
        Log.error("something happened HERE \n" + response.raw());
        response.status(401);
        response.type(MediaType.JSON.getValue());
        return "{\"status\":\"error\"}";
    }
    public static String missingResourcePage(Response response) {
        Log.error("something happened HERE \n" + response.raw());
        response.status(404);
        response.type(MediaType.JSON.getValue());
        response.redirect("/404");
        return "{\"status\":\"error\"}";
    }
    public static String missingResource(Response response){
        Log.error("something happened HERE \n" + response.raw());
        response.status(404);
        response.type(MediaType.JSON.getValue());
        return "{\"status\":\"error\"}";
    }
}
