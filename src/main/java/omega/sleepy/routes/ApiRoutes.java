package omega.sleepy.routes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import omega.sleepy.controllers.ApiController;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import static omega.sleepy.controllers.ApiController.*;
import static spark.Spark.*;

public class ApiRoutes {
    public static void init(){

        Gson gson = new Gson();

        get("api/style", (request, response) -> {
            response.type("text/css");

            return getStyleSheet(response);
        });

        get("/api/blog/tags", (request, response) -> BlogDao.getCategories(), gson::toJson);

        post("/api/blog/content", (request, response) -> saveBlog(request), gson::toJson);

        get("/favicon.ico", (request, response) -> {
            response.type(MediaType.ICON.getValue());
            return getFavicon(response);
        });

        get("api/blog/basic_view", (request, response) -> {
            response.type(MediaType.JSON.getValue());
            return BlogDao.getBlogView();
        }, gson::toJson);

    }




}
