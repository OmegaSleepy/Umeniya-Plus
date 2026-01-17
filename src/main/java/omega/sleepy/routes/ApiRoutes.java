package omega.sleepy.routes;

import com.google.gson.Gson;
import omega.sleepy.controllers.ApiController;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.util.MediaType;

import static omega.sleepy.controllers.ApiController.*;
import static spark.Spark.*;

public class ApiRoutes {
    public static void init(){


        get("api/style", ApiController::getStyleSheet);

        get("/api/blog/tags", ApiController::getCategories);

        post("/api/blog/content", ApiController::createBlog);

        get("/favicon.ico", ApiController::getFavicon);

        get("api/blog/basic_view", ApiController::getFilteredView);

        get("/api/posts/:id", ApiController::getBlogContentsById);

        get("/api/blog/short_blogs", ApiController::getFilteredView);

        get("/api/filter/post/", ApiController::getFilteredView);

    }




}
