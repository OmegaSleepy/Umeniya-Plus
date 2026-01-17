package omega.sleepy.routes;

import com.google.gson.Gson;
import omega.sleepy.controllers.ApiController;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.util.MediaType;

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

        post("/api/blog/content", (request, response) -> createBlog(request), gson::toJson);

        get("/favicon.ico", ApiController::getFavicon);

        get("api/blog/basic_view", (request, response) -> BlogDao.getBlogView(), gson::toJson);

        get("/api/posts/:id", ApiController::getBlogContentsById);

        get("/api/blog/short_blogs", (request, response) -> BlogDao.getBlogWithoutContents(), gson::toJson);

        get("/api/filter/post/", ApiController::getFilteredView, gson::toJson);

    }




}
