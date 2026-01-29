package omega.sleepy.routes;

import omega.sleepy.controllers.ApiController;

import static spark.Spark.*;

public class ApiRoutes {
    public static void init(){

        get("/api/style", ApiController::getStyleSheet);

        get("/favicon.ico", ApiController::getFavicon);

        get("/favicon-logo.ico", ApiController::getFaviconLogo);

        get("/user/info", ApiController::getUserInformation);


        path("/api/blog", () ->{

            post("/create", ApiController::createBlog);

            get("/get-filtered-view/", ApiController::getFilteredView);

            get("/tags", ApiController::getCategories);

            get("/:id", ApiController::getBlogContentsById);

        });

    }

}
