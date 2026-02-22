package omega.sleepy.routes;

import omega.sleepy.controllers.ApiController;

import static spark.Spark.*;

public class ApiRoutes {
    public static void init() {

        get("/api/style/:styleSheet", ApiController::getStyleSheet);
        get("/api/js/:js", ApiController::getJavaScriptFile);
        get("/api/image/:image", ApiController::getImage);


        get("/favicon.ico", ApiController::getFavicon);
        get("/favicon-logo.ico", ApiController::getFavicon);
        get("/api/profile-icon/:icon", ApiController::getIcon);


        get("/api/user/me-info", ApiController::getUserInformation);
        get("/api/user/:user", ApiController::getSpecificUserInformation);


        path("/api/blog", () -> {

            post("/create", ApiController::createBlog);

            get("/get-filtered-view/", ApiController::getFilteredView);
            get("/by-author/:author", ApiController::getByAuthor);

            post("/read", ApiController::rewardReader);

            get("/tags", ApiController::getCategories);

            get("/:id", ApiController::getBlogContentsById);

            get("/can-edit/:id", ApiController::checkCanEdit);
            post("/delete/:id", ApiController::deleteBlog);
            post("/edit/:id", ApiController::editBlog);

        });

    }

}
