package org.martin.controllers;

import org.martin.util.Log;
import org.martin.util.enums.MediaType;
import spark.Response;

/**
 * On the boundary of util and a controller. Controller because of HTTP.
 * Holds common methods for error codes
 * **/
public class ControllerUtil {
    public static String forbitten(Response response) {
        Log.error("something happened HERE \n" + response.raw());
        Log.error("Forbitten");
        response.status(401);
        response.type(MediaType.JSON.getValue());
        return "{\"status\":\"error\"}";
    }
    public static String missingResourcePage(Response response) {
        Log.error("something happened HERE \n" + response.raw());
        Log.error("MissingResourcePage");
        response.status(404);
        response.type(MediaType.JSON.getValue());
        response.redirect("/404");
        return "{\"status\":\"error\"}";
    }
    public static String missingResource(Response response){
        Log.error("something happened HERE \n" + response.raw());
        Log.error("MissingResource");
        response.status(404);
        response.type(MediaType.JSON.getValue());
        return "{\"status\":\"error\"}";
    }
}
