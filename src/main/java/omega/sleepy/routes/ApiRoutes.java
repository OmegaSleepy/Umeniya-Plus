package omega.sleepy.routes;

import omega.sleepy.controllers.ApiController;

import static spark.Spark.*;

public class ApiRoutes {
    public static void init(){

        get("/api/style", ApiController::getStyleSheet);

        get("/favicon.ico", ApiController::getFavicon);


        path("/api/blog", () ->{
            //TODO deprecated, use /api/filer/post/
//            get("basic_view", ApiController::getFilteredView);

            post("/create", ApiController::createBlog);
            //TODO deprecated, use /api/filter/post/
//            get("short_blogs", ApiController::getFilteredView);

            get("/get-filtered-view/", ApiController::getFilteredView);
            get("/tags", ApiController::getCategories);

            get("/:id", ApiController::getBlogContentsById);

        });

    }

}
