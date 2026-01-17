package omega.sleepy.routes;

import omega.sleepy.controllers.ApiController;

import static spark.Spark.*;

public class ApiRoutes {
    public static void init(){

        get("/api/style", ApiController::getStyleSheet);

        get("/favicon.ico", ApiController::getFavicon);

        //TODO rename to /api/blog/:id
        get("/api/posts/:id", ApiController::getBlogContentsById);

        //TODO rename to /api/blog/get-filtered-view/
        get("/api/filter/post/", ApiController::getFilteredView);

        path("/api/blog", () ->{
            //TODO deprecated, use /api/filer/post/
            get("/basic_view", ApiController::getFilteredView);
            //TODO rename to /api/blog/create
            post("/content", ApiController::createBlog);
            //TODO deprecated, use /api/filter/post/
            get("/short_blogs", ApiController::getFilteredView);

            get("/tags", ApiController::getCategories);
        });

    }

}
